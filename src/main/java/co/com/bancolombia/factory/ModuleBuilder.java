package co.com.bancolombia.factory;

import static co.com.bancolombia.Constants.MainFiles.*;
import static co.com.bancolombia.task.GenerateStructureTask.Language.JAVA;
import static co.com.bancolombia.task.GenerateStructureTask.Language.KOTLIN;

import co.com.bancolombia.Constants;
import co.com.bancolombia.adapters.RestService;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.exceptions.ValidationException;
import co.com.bancolombia.factory.validations.Validation;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.utils.FileUpdater;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

public class ModuleBuilder {
  private static final String DEFINITION_FILES = "definition.json";
  private static final String LANGUAGE = "language";
  public static final String LATEST_RELEASE = "latestRelease";
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
  private final RestService restService = new RestService();

  public ModuleBuilder(Project project) {
    this.project = project;
    this.logger = getProject().getLogger();
    params.put("projectName", getProject().getName());
    params.put("projectNameLower", getProject().getName().toLowerCase());
    params.put("pluginVersion", Constants.PLUGIN_VERSION);
    params.put("springBootVersion", Constants.SPRING_BOOT_VERSION);
    params.put("kotlinVersion", Constants.KOTLIN_VERSION);
    params.put("sonarVersion", Constants.SONAR_VERSION);
    params.put("jacocoVersion", Constants.JACOCO_VERSION);
    params.put("gradleVersion", Constants.GRADLE_WRAPPER_VERSION);
    params.put("asyncCommonsStarterVersion", Constants.RCOMMONS_ASYNC_COMMONS_STARTER_VERSION);
    params.put("objectMapperVersion", Constants.RCOMMONS_OBJECT_MAPPER_VERSION);
    params.put("coberturaVersion", Constants.COBERTURA_VERSION);
    params.put("lombokVersion", Constants.LOMBOK_VERSION);
    params.put("commonsJmsVersion", Constants.COMMONS_JMS_VERSION);
    loadPackage();
    loadLanguage();
  }

  public void persist() throws IOException {
    logger.lifecycle("Applying changes on disk");

    logger.lifecycle(
        "files: {}, dirs: {}, deleted dirs: {}", files.size(), dirs.size(), dirsToDelete.size());
    dirs.forEach(
        dir -> {
          getProject().mkdir(dir);
          logger.debug("creating dir {}", dir);
        });
    if (properties != null) {
      logger.lifecycle("Updating application properties");
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
    logger.lifecycle("Changes successfully applied");
    getLatestRelease();
  }

  public void setupFromTemplate(String resourceGroup) throws IOException, ParamNotFoundException {
    TemplateDefinition definition = loadTemplateDefinition(resourceGroup);

    for (String folder : definition.getFolders()) {
      addDir(Utils.fillPath(folder, params));
    }
    Map<String, String> projectFiles = new HashMap<>(definition.getFiles());
    if (isKotlin()) {
      projectFiles.putAll(definition.getKotlin());
    } else {
      projectFiles.putAll(definition.getJava());
    }
    for (Map.Entry<String, String> fileEntry : projectFiles.entrySet()) {
      String path = Utils.fillPath(fileEntry.getValue(), params);
      String content = buildFromTemplate(fileEntry.getKey());
      addDir(Utils.extractDir(path));
      addFile(path, content);
    }
  }

  public void appendToSettings(String module, String baseDir) throws IOException {
    String settingsFile = "settings.gradle" + (isKotlin() ? ".kts" : "");
    logger.lifecycle("adding module {} to " + settingsFile, module);
    String include = isKotlin() ? Utils.INCLUDE_MODULE_KOTLIN : Utils.INCLUDE_MODULE_JAVA;
    updateFile(settingsFile, settings -> Utils.addModule(settings, include, module, baseDir));
  }

  public void removeFromSettings(String module) throws IOException {
    logger.lifecycle("removing {} from settings.gradle", module);
    updateFile(
        "settings.gradle" + (isKotlin() ? ".kts" : ""),
        settings -> {
          String moduleKey = ":" + module;
          return Utils.removeLinesIncludes(settings, moduleKey);
        });
  }

  public void updateExpression(String path, String regex, String value) throws IOException {
    updateFile(path, content -> Utils.replaceExpression(content, regex, value));
  }

  @SneakyThrows
  public Set<String> findExpressions(String path, String regex) {
    logger.lifecycle(
        "find  "
            + Pattern.compile(regex).matcher(readFile(path)).results().count()
            + " dependencies in "
            + path);
    return Pattern.compile(regex)
        .matcher(readFile(path))
        .results()
        .map(MatchResult::group)
        .map(s -> s.replace("'", ""))
        .map(s -> s.replace("\"", ""))
        .collect(Collectors.toSet());
  }

  public void appendDependencyToModule(String module, String dependency) throws IOException {
    logger.lifecycle("adding dependency {} to module {}", dependency, module);
    String buildFilePath = project.getChildProjects().get(module).getBuildFile().getPath();
    buildFilePath = buildFilePath.replace(project.getProjectDir().getPath(), ".");
    if (isKotlin() && !buildFilePath.endsWith(KTS)) {
      buildFilePath += KTS;
    }
    updateFile(buildFilePath, current -> Utils.addDependency(current, dependency));
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
    return (Boolean) params.get(key);
  }

  public boolean isReactive() {
    return getABooleanProperty("reactive", false);
  }

  public boolean isKotlin() {
    return KOTLIN.name().equalsIgnoreCase(params.get(LANGUAGE).toString());
  }

  public boolean isEnableLombok() {
    return getABooleanProperty("lombok", true);
  }

  public boolean withMetrics() {
    return getABooleanProperty("metrics", true);
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

  public void updateFile(String path, FileUpdater updater) throws IOException {
    String content = readFile(path);
    addFile(path, updater.update(content));
  }

  public Release getLatestRelease() {
    if (params.get(LATEST_RELEASE) == null) {
      loadLatestRelease();
    }
    return (Release) params.get(LATEST_RELEASE);
  }

  private void loadPackage() {
    try {
      addParamPackage(FileUtils.readProperties(project.getProjectDir().getPath(), "package"));
    } catch (IOException e) {
      logger.debug("cannot read package from gradle.properties");
    }
  }

  private void loadLanguage() {
    String language = null;
    try {
      language = FileUtils.readProperties(project.getProjectDir().getPath(), LANGUAGE);
    } catch (IOException e) {
      logger.debug("cannot read language from gradle.properties");
    }
    if (language == null) {
      language = JAVA.name().toLowerCase();
    }
    this.params.put(LANGUAGE, language);
  }

  private void loadLatestRelease() {
    Release latestRelease = restService.getLatestPluginVersion();
    if (latestRelease != null && !latestRelease.getTagName().equals(Utils.getVersionPlugin())) {
      logger.lifecycle(
          "WARNING: You have an old version of the plugin, the latest version is: {}",
          latestRelease.getTagName());
      params.put(LATEST_RELEASE, latestRelease);
    }
  }

  private boolean getABooleanProperty(String property, boolean defaultValue) {
    try {
      String value = FileUtils.readProperties(project.getProjectDir().getPath(), property);
      return "true".equals(value);
    } catch (IOException e) {
      logger.warn(e.getMessage());
      logger.lifecycle(
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
      content = FileUtils.readFile(getProject(), finalPath).collect(Collectors.joining("\n"));
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
