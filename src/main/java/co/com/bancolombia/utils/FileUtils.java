package co.com.bancolombia.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
  private static final String GRADLE_PROPERTIES = "/gradle.properties";

  public static void writeString(Project project, String filePath, String content)
      throws IOException {
    project.getLogger().debug(project.file(filePath).getAbsolutePath());
    File file = project.file(filePath).getAbsoluteFile();
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
    File[] directories = new File(dirPath).getAbsoluteFile().listFiles(File::isDirectory);
    FilenameFilter filter = (file, s) -> s.contains("build.gradle");
    List<File> textFiles = new ArrayList<>();
    if (directories != null) {
      for (File dir : directories) {
        textFiles.addAll(Arrays.asList(dir.listFiles(filter)));
      }
    }
    return textFiles;
  }

  public static String readProperties(String projectPath, String variable) throws IOException {
    Properties properties = new Properties();
    try (BufferedReader br = new BufferedReader(new FileReader(projectPath + GRADLE_PROPERTIES))) {
      properties.load(br);
    }
    if (properties.getProperty(variable) != null) {
      return properties.getProperty(variable);
    } else {
      throw new IOException("No parameter " + variable + " in gradle.properties file");
    }
  }

  public static void setGradleProperty(String projectPath, String variable, String value)
      throws IOException {
    try (FileInputStream fis = new FileInputStream(projectPath + GRADLE_PROPERTIES)) {
      Properties properties = new Properties();
      properties.load(fis);
      properties.setProperty(variable, value);
      try (FileOutputStream fos = new FileOutputStream(projectPath + GRADLE_PROPERTIES)) {
        properties.store(fos, null);
      }
    }
  }

  public static String getResourceAsString(DefaultResolver resolver, String path)
      throws IOException {
    Reader reader = resolver.getReader(path);
    return IOUtils.toString(reader);
  }

  public static ObjectNode getFromYaml(File file) throws IOException {
    ObjectMapper mapper =
        new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    Object object = mapper.readTree(file);
    return (ObjectNode) object;
  }

  public static String parseToYaml(ObjectNode node) throws IOException {
    ObjectMapper mapper =
        new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    return mapper.writeValueAsString(node);
  }

  public static boolean exists(String dir, String file) {
    return Files.exists(Paths.get(dir, file));
  }

  public static String toRelative(String path) {
    if (path.startsWith("./")) {
      return path;
    }
    if (path.startsWith(".\\")) {
      return path;
    }
    if (Paths.get(path).isAbsolute()) {
      return path;
    }
    return "./" + path;
  }
}
