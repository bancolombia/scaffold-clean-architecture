package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.templates.PluginTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.DefaultResolver;
import org.apache.commons.io.IOUtils;
import org.gradle.api.DefaultTask;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenerateBaseTask extends DefaultTask {
    private static final String DEFINITION_FILE = "definition.json";
    private static final String PARAM_START = "{{";
    private static final String PARAM_END = "}}";
    private static final int PARAM_LENGTH = 2;
    private final DefaultResolver resolver = new DefaultResolver();
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private final List<FileModel> files = new ArrayList<>();
    private final List<String> dirs = new ArrayList<>();
    private final Map<String, Object> params = new HashMap<>();

    public GenerateBaseTask() {
        super();
        params.put("pluginVersion", PluginTemplate.VERSION_PLUGIN);
        params.put("springBootVersion", PluginTemplate.SPRING_BOOT_VERSION);
        params.put("springCloudVersion", PluginTemplate.SPRING_CLOUD_VERSION);
        params.put("sonarVersion", PluginTemplate.SONAR_VERSION);
        params.put("jacocoVersion", PluginTemplate.JACOCO_VERSION);
    }

    protected abstract void setupDirs();

    protected abstract void setupFiles() throws CleanException, IOException;

    protected void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    protected void addParamPackage(String packageName) {
        this.params.put("package", packageName.toLowerCase());
        this.params.put("packagePath", packageName.replaceAll("\\.", "\\/").toLowerCase());
    }

    protected String getPackage() {
        return (String) params.get("package");
    }

    protected String getPackagePath() {
        return (String) params.get("packagePath");
    }

    protected void addFile(String path, String content) {
        this.files.add(FileModel.builder()
                .path(path)
                .content(content)
                .build());
    }

    @SuppressWarnings("unchecked")
    protected void addFileTemplates(String resourceGroup) throws IOException, ParamNotFoundException {
        Reader reader = resolver.getReader(Utils.joinPath(resourceGroup, DEFINITION_FILE));
        String targetString = IOUtils.toString(reader);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.readValue(targetString, Map.class);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            this.files.add(FileModel.builder()
                    .path(fillPath(entry.getValue()))
                    .content(buildFromTemplate(entry.getKey()))
                    .build());
        }
    }

    protected String fillPath(String path) throws ParamNotFoundException {
        while (path.contains(PARAM_START)) {
            String key = path.substring(path.indexOf(PARAM_START) + PARAM_LENGTH, path.indexOf(PARAM_END));
            if (params.containsKey(key)) {
                path = path.replace(PARAM_START + key + PARAM_END, params.get(key).toString());
            } else {
                throw new ParamNotFoundException(key);
            }
        }
        return path;
    }

    protected void addDir(String path) {
        this.dirs.add(path);
    }

    protected void addDir(String... path) {
        this.dirs.add(Utils.joinPath(path));
    }

    protected void createDirs() {
        setupDirs();
        dirs.forEach(getProject()::mkdir);
    }

    protected void createFiles() throws IOException, CleanException {
        setupFiles();
        for (FileModel file : files) {
            Utils.writeString(getProject(), file.getPath(), file.getContent());
        }
    }

    private String buildFromTemplate(String resource) {
        Mustache mustache = mustacheFactory.compile(resource);
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, params);
        return stringWriter.toString();
    }
}
