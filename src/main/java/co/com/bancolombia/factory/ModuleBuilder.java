package co.com.bancolombia.factory;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

public class ModuleBuilder {
    private static final String DEFINITION_FILES = "definition.json";
    private final DefaultResolver resolver = new DefaultResolver();
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private final List<FileModel> files = new ArrayList<>();
    private final List<String> dirs = new ArrayList<>();
    private final Map<String, Object> params = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger;
    @Getter
    private final Project project;

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
        for (FileModel file : files) {
            Utils.writeString(getProject(), file.getPath(), file.getContent());
        }
        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);
    }

    @SuppressWarnings("unchecked")
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

    public void appendSettings(String module, String baseDir) throws IOException {
        String settings = Utils.readFile(getProject(), Constants.SETTINGS_GRADLE)
                .collect(Collectors.joining("\n"));
        settings += "\ninclude ':" + module + "'\n" +
                "project(':" + module + "').projectDir = file('./" + baseDir + "/" + module + "')";
        addFile(Constants.SETTINGS_GRADLE, settings);
    }

    public void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    public void addParamPackage(String packageName) {
        this.params.put("package", packageName.toLowerCase());
        this.params.put("packagePath", packageName.replaceAll("\\.", "\\/").toLowerCase());
    }

    public void addFile(String path, String content) {
        this.files.add(FileModel.builder()
                .path(path)
                .content(content)
                .build());
    }

    public void addDir(String path) {
        if (path != null) {
            this.dirs.add(path);
        }
    }

    public void addDir(String... path) {
        this.dirs.add(Utils.joinPath(path));
    }

    public String getPackage() {
        return (String) params.get("package");
    }

    public String getPackagePath() {
        return (String) params.get("packagePath");
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
