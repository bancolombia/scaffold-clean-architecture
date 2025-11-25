package co.com.bancolombia.factory;

import static co.com.bancolombia.Constants.MainFiles.APPLICATION_PROPERTIES;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Description;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Header;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Normal;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Success;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.exceptions.ValidationException;
import co.com.bancolombia.factory.adapters.DrivenAdapterSecrets;
import co.com.bancolombia.factory.validations.Validation;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.task.AbstractCleanArchitectureDefaultTask;
import co.com.bancolombia.utils.FileUpdater;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.OperationsProvider;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class ModuleBuilder {
  private static final String DEFINITION_FILES = "definition.json";
  public static final String LATEST_RELEASE = "latestRelease";
  public static final String SETTINGS_GRADLE = "settings.gradle";
  private final DefaultResolver resolver = new DefaultResolver();
  private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
  private final Map<String, FileModel> files = new ConcurrentHashMap<>();
  private final List<String> dirs = new ArrayList<>();
  private final List<String> dirsToDelete = new ArrayList<>();
  private final Map<String, Object> params = new HashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();
  private final Logger logger;
  @Getter private final Project project;
  private ObjectNode properties;

  @Setter private StyledTextOutput styledLogger;
  private final ExternalOperations operations;

  public ModuleBuilder(Project project) {
    this.project = project;
    this.logger = getProject().getLogger();
    this.operations = OperationsProvider.fromDefault();
    initialize();
  }

  public ModuleBuilder(Project project, ExternalOperations operations) {
    this.project = project;
    this.logger = getProject().getLogger();
    this.operations = operations;
    initialize();
  }

  private void initialize() {
    params.put("projectName", getProject().getName());
    params.put("projectNameLower", getProject().getName().toLowerCase());
    params.put("lombok", isEnableLombok());
    params.put("metrics", withMetrics());
    addConstantsFromClassToModuleBuilder(this, Constants.class);
    loadPackage();
    loadIsExample();
  }

  public static void addConstantsFromClassToModuleBuilder(ModuleBuilder builder, Class<?> clazz) {
    Arrays.stream(clazz.getDeclaredFields())
        .filter(
            field ->
                Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
        .forEach(field -> builder.addParam(field.getName(), extract(field)));
  }

  @SneakyThrows
  private static Object extract(Field field) {
    return field.get(field.getType());
  }

  public void persist() throws IOException {
    styledLogger.style(Header).println("Applying changes on disk");

    styledLogger
        .style(Header)
        .append("files: ")
        .style(Success)
        .append(Integer.toString(files.size()))
        .style(Header)
        .append(", dirs: ")
        .style(Success)
        .append(Integer.toString(dirs.size()))
        .style(Header)
        .append(", deleted dirs: ")
        .style(Success)
        .append(Integer.toString(dirsToDelete.size()))
        .println();

    dirs.forEach(
        dir -> {
          getProject().mkdir(dir);
          logger.debug("creating dir {}", dir);
        });
    if (properties != null) {
      styledLogger.style(Normal).println("Updating application properties");
      addFile(APPLICATION_PROPERTIES, FileUtils.parseToYaml(properties));
    }

    files.forEach(
        (key, file) -> {
          try {
            FileUtils.writeString(getProject(), file.getPath(), file.getContent());
          } catch (IOException e) {
            logger.error("error to write file {}", file.getPath());
            throw new RuntimeException(e.getMessage(), e);
          }
          logger.debug("file {} written", file.getPath());
        });

    dirsToDelete.forEach(
        dir -> {
          getProject().delete(dir);
          logger.debug("deleting dir {}", dir);
        });
    styledLogger.style(Success).println("Changes successfully applied");
    getLatestRelease();
  }

  public void setupFromTemplate(String resourceGroup) throws IOException, ParamNotFoundException {
    TemplateDefinition definition = loadTemplateDefinition(resourceGroup);

    for (String folder : definition.getFolders()) {
      addDir(Utils.fillPath(folder, params));
    }
    Map<String, String> projectFiles = new HashMap<>(definition.getFiles());
    for (Map.Entry<String, String> fileEntry : projectFiles.entrySet()) {
      String path = Utils.fillPath(fileEntry.getValue(), params);
      String content = buildFromTemplate(fileEntry.getKey());
      addDir(Utils.extractDir(path));
      addFile(path, content);
    }
  }

  public void appendToSettings(String module, String baseDir) throws IOException {
    logger.lifecycle("adding module {} to " + SETTINGS_GRADLE, module);
    updateFile(
        SETTINGS_GRADLE,
        settings -> Utils.addModule(settings, Utils.INCLUDE_MODULE_JAVA, module, baseDir));
  }

  public void removeFromSettings(String module) throws IOException {
    logger.lifecycle("removing {} from settings.gradle", module);
    updateFile(
        SETTINGS_GRADLE,
        settings -> {
          String moduleKey = ":" + module;
          return Utils.removeLinesIncludes(settings, moduleKey);
        });
  }

  public boolean updateExpression(String path, String regex, String value) throws IOException {
    return updateFile(path, content -> Utils.replaceExpression(content, regex, value));
  }

  @SneakyThrows
  public Set<String> findExpressions(String path, String regex) {
    logger.debug(
        "{} dependencies found in {}",
        Pattern.compile(regex).matcher(readFile(path)).results().count(),
        path);
    return Utils.findExpressions(readFile(path), regex);
  }

  public String getSecretsBackendEnabled() {
    String fileName = "applications/app-service/build.gradle";
    if (!findExpressions(fileName, "com.github.bancolombia:aws-secrets").isEmpty()) {
      return "AWS_SECRETS_MANAGER";
    } else if (!findExpressions(fileName, "com.github.bancolombia:vault").isEmpty()) {
      return "VAULT";
    } else {
      return "NONE";
    }
  }

  public void setUpSecretsInAdapter() throws CleanException, IOException {
    boolean includeSecrets = getBooleanParam("include-secret");
    if (!includeSecrets) {
      return;
    }
    String secretsBackend = getSecretsBackendEnabled();
    if (secretsBackend.equals("NONE")) {
      new DrivenAdapterSecrets().buildModule(this);
      // when new secrets backend is added, the default is aws
      addParam("include-awssecrets", AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    } else {
      addParam("include-awssecrets", "AWS_SECRETS_MANAGER".equalsIgnoreCase(secretsBackend));
      addParam("include-vaultsecrets", "VAULT".equalsIgnoreCase(secretsBackend));
    }
  }

  public void appendDependencyToModule(String module, String dependency) throws IOException {
    String buildFilePath = project.getChildProjects().get(module).getBuildFile().getPath();
    buildFilePath = buildFilePath.replace(project.getProjectDir().getPath(), ".");
    updateFile(
        buildFilePath,
        current -> {
          String res = Utils.addDependency(current, dependency);
          if (!current.equals(res)) {
            logger.lifecycle("adding dependency {} to module {}", dependency, module);
          }
          return res;
        });
  }

  public void appendConfigurationToModule(String module, String configuration) throws IOException {
    logger.lifecycle("adding configuration {} to module {}", configuration, module);
    String buildFilePath = project.getChildProjects().get(module).getBuildFile().getPath();
    updateFile(buildFilePath, current -> Utils.addConfiguration(current, configuration));
  }

  public void removeDependencyFromModule(String module, String dependency) throws IOException {
    logger.lifecycle("removing dependency {} from module {}", dependency, module);
    String buildFilePath = project.getChildProjects().get(module).getBuildFile().getPath();
    updateFile(buildFilePath, current -> Utils.removeLinesIncludes(current, dependency));
  }

  public void deleteModule(String module) {
    String projectDir = project.getChildProjects().get(module).getProjectDir().getPath();
    logger.lifecycle(
        "deleting module {} from dir {}",
        module,
        projectDir.replace(project.getProjectDir().getPath(), ""));
    removeDir(projectDir);
  }

  public ObjectNode appendToProperties(String path) throws IOException {
    if (properties == null) {
      File yamlFile = project.file(APPLICATION_PROPERTIES);
      properties = FileUtils.getFromYaml(yamlFile);
    }
    if (path.isEmpty()) {
      return properties;
    }
    List<String> attributes = new ArrayList<>(Arrays.asList(path.split("\\.")));
    return getNode(properties, attributes);
  }

  public void addParam(String key, Object value) {
    this.params.put(key, value);
  }

  public void addParamPackage(String packageName) {
    this.params.put("package", packageName.toLowerCase());
    this.params.put("packagePath", packageName.replace('.', '/').toLowerCase());
  }

  public void addFile(String path, String content) {
    String finalPath = FileUtils.toRelative(path);
    this.files.put(finalPath, FileModel.builder().path(finalPath).content(content).build());
  }

  public void addDir(String path) {
    if (path != null) {
      this.dirs.add(path);
    }
  }

  public void removeDir(String path) {
    if (path != null) {
      this.dirsToDelete.add(path);
    }
  }

  public String getStringParam(String key) {
    return (String) params.get(key);
  }

  public Object getParam(String key) {
    return params.get(key);
  }

  public boolean getBooleanParam(String key) {
    return (Boolean) params.getOrDefault(key, false);
  }

  public boolean isReactive() {
    return getABooleanProperty("reactive", false);
  }

  public boolean analyticsEnabled() throws IOException {
    String value = FileUtils.readProperties(project.getProjectDir().getPath(), "analytics");
    return "true".equals(value);
  }

  public boolean isEnableLombok() {
    return getABooleanProperty("lombok", true);
  }

  public boolean withMetrics() {
    return getABooleanProperty("metrics", true);
  }

  public boolean withMutation() {
    return getABooleanProperty("mutation", false);
  }

  @SafeVarargs
  public final <T extends Validation> void runValidations(Class<T>... validations)
      throws ValidationException {
    for (Iterator<Class<T>> it = Arrays.stream(validations).iterator(); it.hasNext(); ) {
      try {
        T validation = it.next().getDeclaredConstructor().newInstance();
        validation.validate(this);
      } catch (InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException e) {
        logger.warn("Error instantiating validator: {}", e.getMessage());
      }
    }
  }

  public boolean updateFile(String path, FileUpdater updater) throws IOException {
    String content = readFile(path);
    String newContent = updater.update(content);
    boolean changed = !content.equals(newContent);
    if (changed) {
      addFile(path, newContent);
    }
    return changed;
  }

  public Release getLatestRelease() {
    if (params.get(LATEST_RELEASE) == null) {
      loadLatestRelease();
    }
    return (Release) params.get(LATEST_RELEASE);
  }

  public void runTask(String name) {
    this.runTask(name, getProject().getProjectDir());
  }

  public void runTask(String name, String projectPath) {
    File projectDir = new File(projectPath);
    this.runTask(name, projectDir);
  }

  private void runTask(String name, File projectDir) {
    logger.lifecycle("Connecting to project to run task {}", name);
    logger.lifecycle("Project Directory {}", projectDir);
    try (ProjectConnection connection =
        GradleConnector.newConnector()
            .useGradleVersion(Constants.GRADLE_WRAPPER_VERSION)
            .forProjectDirectory(projectDir)
            .connect()) {
      logger.lifecycle("Connected! executing task {}", name);
      connection.newBuild().forTasks(name).run();
    } catch (Exception e) {
      logger.warn(
          "Error executing 'gradle {}', please run it you manually: {}", name, e.getMessage());
    }
  }

  private void loadPackage() {
    try {
      addParamPackage(FileUtils.readProperties(project.getProjectDir().getPath(), "package"));
    } catch (IOException e) {
      logger.debug("cannot read package from gradle.properties");
    }
  }

  private void loadIsExample() {
    final String param = "example";
    try {
      this.params.put(
          param, "true".equals(FileUtils.readProperties(project.getProjectDir().getPath(), param)));
    } catch (IOException e) {
      logger.debug("cannot read example from gradle.properties");
      this.params.put(param, false);
    }
  }

  private void loadLatestRelease() {
    Release latestRelease = operations.getLatestPluginVersion();
    if (latestRelease != null) {
      if (latestRelease.getTagName().equals(Utils.getVersionPlugin())) {
        logger.lifecycle("You have the latest plugin version {}", latestRelease.getTagName());
      } else {
        styledLogger
            .style(Description)
            .append("You have an old version of the plugin ")
            .style(Normal)
            .append("the latest version is: ")
            .style(Header)
            .append(latestRelease.getTagName())
            .style(Normal)
            .append(" to update it please run: ")
            .style(Success)
            .append("gradle u")
            .println();
      }
      params.put(LATEST_RELEASE, latestRelease);
    }
  }

  private boolean getABooleanProperty(String property, boolean defaultValue) {
    try {
      String value = FileUtils.readProperties(project.getProjectDir().getPath(), property);
      return "true".equals(value);
    } catch (IOException e) {
      logger.info(e.getMessage());
      logger.info(
          "WARN: variable "
              + property
              + " not present, if your project use "
              + property
              + " please add "
              + property
              + "=true to gradle.properties and relaunch this task");
      return defaultValue;
    }
  }

  private String readFile(String path) throws IOException {
    String finalPath = FileUtils.toRelative(path);
    FileModel current = files.get(finalPath);
    String content;
    if (current == null) {
      content = FileUtils.readFile(getProject(), finalPath);
    } else {
      content = current.getContent();
    }
    return content;
  }

  private ObjectNode getNode(ObjectNode node, List<String> attributes) {
    if (attributes.isEmpty()) {
      return node;
    } else {
      String attribute = attributes.remove(0);
      ObjectNode current =
          node.has(attribute) ? (ObjectNode) node.get(attribute) : node.putObject(attribute);
      return getNode(current, attributes);
    }
  }

  private String buildFromTemplate(String resource) {
    Mustache mustache = mustacheFactory.compile(resource);
    StringWriter stringWriter = new StringWriter();
    mustache.execute(stringWriter, params);
    return stringWriter.toString();
  }

  private TemplateDefinition loadTemplateDefinition(String resourceGroup) throws IOException {
    String targetString =
        FileUtils.getResourceAsString(resolver, Utils.joinPath(resourceGroup, DEFINITION_FILES));
    return mapper.readValue(targetString, TemplateDefinition.class);
  }
}
