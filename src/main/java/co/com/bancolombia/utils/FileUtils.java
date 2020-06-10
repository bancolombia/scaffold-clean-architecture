package co.com.bancolombia.utils;

import co.com.bancolombia.templates.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gradle.api.Project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class FileUtils {

    public static void writeString(Project project, String filePath, String content) throws IOException {
        project.getLogger().debug(project.file(filePath).getAbsolutePath());
        File file = project
                .file(filePath)
                .getAbsoluteFile();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        }
    }

    public static Stream<String> readFile(Project project, String filePath) throws IOException {
        File file = project.file(filePath).getAbsoluteFile();
        project.getLogger().debug(file.getAbsolutePath());
        return Files.lines(Paths.get(file.toURI()));
    }

    public static List<File> finderSubProjects(String dirPath) {
        File[] directories = new File(dirPath)
                .getAbsoluteFile()
                .listFiles(File::isDirectory);
        FilenameFilter filter = (file, s) -> s.endsWith("build.gradle");
        List<File> textFiles = new ArrayList<>();
        for (File dir : directories) {
            textFiles.addAll(Arrays.asList(dir.listFiles(filter)));
        }
        return textFiles;
    }

    public static String readProperties(String variable) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("gradle.properties"));
        if (properties.getProperty(variable) != null) {
            return properties.getProperty(variable);
        } else {
            throw new IOException("No parameter" + variable + " in build.properties file");
        }
    }

    public static ObjectNode getApplicationYaml(Project project) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        String yamlString = readFile(project, Constants.APPLICATION_PROPERTIES)
                .collect(Collectors.joining());
        System.out.println(yamlString);
        Object object = mapper.readTree(project.file(Constants.APPLICATION_PROPERTIES));
        return (ObjectNode) object;
    }

    public static String parseApplicationYaml(ObjectNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        return mapper.writeValueAsString(node);
    }
}
