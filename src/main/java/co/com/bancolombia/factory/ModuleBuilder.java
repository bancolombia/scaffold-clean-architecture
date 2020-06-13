package co.com.bancolombia.factory;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.utils.FileAppender;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.DefaultResolver;
import lombok.Getter;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModuleBuilder {
    private static final String APPLICATION_PROPERTIES = "applications/app-service/src/main/resources/application.yaml";
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
        params.put("pluginVersion", Constants.PLUGIN_VERSION);
        params.put("springBootVersion", Constants.SPRING_BOOT_VERSION);
        params.put("springCloudVersion", Constants.SPRING_CLOUD_VERSION);
        params.put("sonarVersion", Constants.SONAR_VERSION);
        params.put("jacocoVersion", Constants.JACOCO_VERSION);
    }

    public void persist() throws IOException {
        logger.lifecycle("Generating dirs");
        dirs.forEach(getProject()::mkdir);
        logger.lifecycle("Dirs generated");
        logger.lifecycle("Generating files");
        if (properties != null) {
            addFile(APPLICATION_PROPERTIES, FileUtils.parseToYaml(properties));
        }
        for (Map.Entry<String, FileModel> fileEntry : files.entrySet()) {
            FileModel file = fileEntry.getValue();
            FileUtils.writeString(getProject(), file.getPath(), file.getContent());
            logger.debug("file {} written", file.getPath());
        }
        logger.lifecycle("Files written");
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
        appendToFile("settings.gradle", settings -> settings + ("\ninclude ':" + module + "'\n" +
                "project(':" + module + "').projectDir = file('./" + baseDir + "/" + module + "')"));
    }

    public void appendDependencyToModule(String module, String dependency) throws IOException {
        String buildFilePath = project.getChildProjects().get(module).getBuildFile().getPath();
        appendToFile(buildFilePath, current -> Utils.addDependency(current, dependency));
    }

    public ObjectNode appendToProperties(String path) throws IOException {
        if (properties == null) {
            File yamlFile = project.file(APPLICATION_PROPERTIES);
            properties = FileUtils.getFromYaml(yamlFile);
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

    public String getStringParam(String key) {
        return (String) params.get(key);
    }

    private void appendToFile(String path, FileAppender appender) throws IOException {
        FileModel current = files.get(path);
        String content;
        if (current == null) {
            content = FileUtils.readFile(getProject(), path)
                    .collect(Collectors.joining("\n"));
        } else {
            content = current.getContent();
        }
        addFile(path, appender.append(content));
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
        String targetString = FileUtils.getResourceAsString(resolver, Utils.joinPath(resourceGroup, DEFINITION_FILES));
        return mapper.readValue(targetString, TemplateDefinition.class);
    }
}
