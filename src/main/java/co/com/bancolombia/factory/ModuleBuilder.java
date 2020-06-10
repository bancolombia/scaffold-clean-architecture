package co.com.bancolombia.factory;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.DefaultResolver;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModuleBuilder {
    private static final String DEFINITION_FILES = "definition.json";
    private final DefaultResolver resolver = new DefaultResolver();
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private final Map<String, FileModel> files = new ConcurrentHashMap<>();
    private final List<String> dirs = new ArrayList<>();
    private final Map<String, Object> params = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger;
    @Getter
    private final Project project;
    private ObjectNode properties;

    public ModuleBuilder(Project project) {
        this.project = project;
        this.logger = getProject().getLogger();
        params.put("projectName", getProject().getName());
        params.put("projectNameLower", getProject().getName().toLowerCase());
        params.put("pluginVersion", PluginTemplate.VERSION_PLUGIN);
        params.put("springBootVersion", PluginTemplate.SPRING_BOOT_VERSION);
        params.put("springCloudVersion", PluginTemplate.SPRING_CLOUD_VERSION);
        params.put("sonarVersion", PluginTemplate.SONAR_VERSION);
        params.put("jacocoVersion", PluginTemplate.JACOCO_VERSION);
    }

    public void persist() throws IOException {
        logger.lifecycle(PluginTemplate.GENERATING_CHILDS_DIRS);
        dirs.forEach(getProject()::mkdir);
        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        logger.lifecycle(PluginTemplate.GENERATING_FILES);
        if (properties != null) {
            addFile(Constants.APPLICATION_PROPERTIES, FileUtils.parseApplicationYaml(properties));
        }
        for (Map.Entry<String, FileModel> fileEntry : files.entrySet()) {
            FileModel file = fileEntry.getValue();
            FileUtils.writeString(getProject(), file.getPath(), file.getContent());
            logger.debug("file {} written", file.getPath());
        }
        logger.lifecycle(PluginTemplate.WRITTEN_FILES);
    }

    public void setupFromTemplate(String resourceGroup) throws IOException, ParamNotFoundException {
        TemplateDefinition definition = loadTemplateDefinition(resourceGroup);
        for (String folder : definition.getFolders()) {
            addDir(Utils.fillPath(folder, params));
        }
        for (Map.Entry<String, String> fileEntry : definition.getFiles().entrySet()) {
            String path = Utils.fillPath(fileEntry.getValue(), params);
            String content = buildFromTemplate(fileEntry.getKey());
            addDir(Utils.extractDir(path));
            addFile(path, content);
        }
    }

    public void appendToSettings(String module, String baseDir) throws IOException {
        FileModel current = files.get(Constants.SETTINGS_GRADLE);
        String settings;
        if (current == null) {
            settings = FileUtils.readFile(getProject(), Constants.SETTINGS_GRADLE)
                    .collect(Collectors.joining("\n"));
        } else {
            settings = current.getContent();
        }
        settings += "\ninclude ':" + module + "'\n" +
                "project(':" + module + "').projectDir = file('./" + baseDir + "/" + module + "')";
        addFile(Constants.SETTINGS_GRADLE, settings);
    }

    public ObjectNode appendToProperties(String path) throws IOException {
        if (properties == null) {
            properties = FileUtils.getApplicationYaml(getProject());
        }
        if (path.isEmpty()) {
            return properties;
        }
        List<String> attributes = new ArrayList<>(List.of(path.split("\\.")));
        return getNode(properties, attributes);
    }

    public void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    public void addParamPackage(String packageName) {
        this.params.put("package", packageName.toLowerCase());
        this.params.put("packagePath", packageName.replaceAll("\\.", "\\/").toLowerCase());
    }

    public void addFile(String path, String content) {
        this.files.put(path, FileModel.builder()
                .path(path)
                .content(content)
                .build());
    }

    public void addDir(String path) {
        if (path != null) {
            this.dirs.add(path);
        }
    }

    public String getPackage() {
        return (String) params.get("package");
    }

    public String getPackagePath() {
        return (String) params.get("packagePath");
    }

    private ObjectNode getNode(ObjectNode node, List<String> attributes) {
        if (attributes.isEmpty()) {
            return node;
        } else {
            String attribute = attributes.remove(0);
            ObjectNode current = node.has(attribute) ? (ObjectNode) node.get(attribute) : node.putObject(attribute);
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
        Reader reader = resolver.getReader(Utils.joinPath(resourceGroup, DEFINITION_FILES));
        String targetString = IOUtils.toString(reader);
        return mapper.readValue(targetString, TemplateDefinition.class);
    }
}
