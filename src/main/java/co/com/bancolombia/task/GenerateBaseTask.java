package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.templates.PluginTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.DefaultResolver;
import org.apache.commons.io.IOUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateBaseTask extends DefaultTask {
    private static final String DEFINITION_FILES = "definition.json";
    protected final Logger logger = getProject().getLogger();
    private final DefaultResolver resolver = new DefaultResolver();
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private final List<FileModel> files = new ArrayList<>();
    private final List<String> dirs = new ArrayList<>();
    private final Map<String, Object> params = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public GenerateBaseTask() {
        super();
        params.put("pluginVersion", PluginTemplate.VERSION_PLUGIN);
        params.put("springBootVersion", PluginTemplate.SPRING_BOOT_VERSION);
        params.put("springCloudVersion", PluginTemplate.SPRING_CLOUD_VERSION);
        params.put("sonarVersion", PluginTemplate.SONAR_VERSION);
        params.put("jacocoVersion", PluginTemplate.JACOCO_VERSION);
    }

    protected void executeTask() throws IOException {
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
    protected void setupFromTemplate(String resourceGroup) throws IOException, ParamNotFoundException {
        TemplateDefinition definition = loadTemplateDefinition(resourceGroup);
        for (String folder : definition.getFolders()) {
            addDir(Utils.fillPath(folder, params));
        }
        for (Map.Entry<String, String> fileEntry : definition.getFiles().entrySet()) {
            String path = Utils.fillPath(fileEntry.getValue(), params);
            String content = buildFromTemplate(fileEntry.getKey());
            addFile(path, content);
        }
    }

    protected void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    protected void addParamPackage(String packageName) {
        this.params.put("package", packageName.toLowerCase());
        this.params.put("packagePath", packageName.replaceAll("\\.", "\\/").toLowerCase());
    }

    protected void addFile(String path, String content) {
        this.files.add(FileModel.builder()
                .path(path)
                .content(content)
                .build());
    }

    protected void addDir(String path) {
        this.dirs.add(path);
    }

    protected void addDir(String... path) {
        this.dirs.add(Utils.joinPath(path));
    }

    protected String getPackage() {
        return (String) params.get("package");
    }

    protected String getPackagePath() {
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
