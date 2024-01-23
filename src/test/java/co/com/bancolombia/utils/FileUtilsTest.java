package co.com.bancolombia.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

  @Test
  void readPropertiesExist() throws Exception {
    String property = "package";
    assertEquals("co.com.bancolombia", FileUtils.readProperties(".", property));
  }

  @Test
  void readPropertiesNonExists() {
    String property = "packageName";
    assertThrows(IOException.class, () -> FileUtils.readProperties("build", property));
  }

  @Test
  void readFile() throws IOException {
    Project project =
        ProjectBuilder.builder().withProjectDir(new File("src/test/resources")).build();
    String response = FileUtils.readFile(project, "temp.txt");

    assertEquals("hello", response);
  }

  @Test
  void readFileFromResources() throws IOException {
    DefaultResolver resolver = new DefaultResolver();
    String response = FileUtils.getResourceAsString(resolver, "temp.txt");

    assertEquals("hello", response);
  }

  @Test
  void readYaml() throws IOException {
    File file = new File("src/test/resources/application.yaml");
    ObjectNode yaml = FileUtils.getFromYaml(file);

    assertEquals(8080, yaml.get("server").get("port").asInt());
  }

  @Test
  void parseToYaml() throws IOException {
    File file = new File("src/test/resources/application.yaml");
    ObjectNode yaml = FileUtils.getFromYaml(file);
    ((ObjectNode) yaml.get("server")).put("port", 8081);
    String expected = "server:\n  port: 8081";

    String result = FileUtils.parseToYaml(yaml);

    assertEquals(expected, result.substring(0, expected.length()));
  }

  @Test
  void writeString() throws IOException {
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/tmp")).build();
    FileUtils.writeString(project, "temp.txt", "hello");
    String response = FileUtils.readFile(project, "temp.txt");

    assertEquals("hello", response);
  }

  @Test
  void finderSubProjects() {
    List<File> files = FileUtils.finderSubProjects("src/test/resources");

    assertEquals(0, files.size());

    List<File> files2 = FileUtils.finderSubProjects("src/test/resources/finderSubProjects/");

    assertEquals(2, files2.size());
  }

  // Assert
  @Test
  void shouldHandleErrorWhenNotParamReplacePlaceholders() {
    // Arrange
    String fillablePath = "default/driven-adapters/{{name}}/src/main/{{className}}";
    Map<String, Object> params = new HashMap<>();
    params.put("className", "Redis.java");
    // Act
    assertThrows(ParamNotFoundException.class, () -> Utils.fillPath(fillablePath, params));
  }

  @Test
  void shouldExtractDir() {
    // Arrange
    String classPath = "default/driven-adapters/package/src/main/Model.java";
    // Act
    String result = Utils.extractDir(classPath);
    // Assert
    assertEquals("default/driven-adapters/package/src/main", result);
  }

  @Test
  void shouldExtractDirWhenNoPath() {
    // Arrange
    String classPath = "Model.java";
    // Act
    String result = Utils.extractDir(classPath);
    // Assert
    assertNull(result);
  }

  @Test
  void shouldFormatTaskOptions() {
    // Arrange
    List<?> options = Arrays.asList(Options.values());
    // Act
    String result = Utils.formatTaskOptions(options);
    // Assert
    assertEquals("[A|BC|D]", result);
  }

  @Test
  void shouldFormatTaskOptionsSingle() {
    // Arrange
    List<?> options = Collections.singletonList("A");
    // Act
    String result = Utils.formatTaskOptions(options);
    // Assert
    assertEquals("[A]", result);
  }

  @Test
  void shouldAddDependency() {
    // Arrange
    String build =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}";
    String expected =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':my-module')\n"
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}";
    // Act
    String result = Utils.addDependency(build, "implementation project(':my-module')");
    // Assert
    assertEquals(expected, result);
  }

  @Test
  void shouldReadContentFromZip() throws IOException {
    // Arrange
    String zipFilePath = "build/sample.zip";
    String textFileName = "sample.txt";
    String textContent = "This is the content of the text file.";
    Path tempFilePath = createTempTextFile("build/" + textFileName, textContent);
    createZipFile(zipFilePath, tempFilePath, textFileName);
    // Act
    String result = FileUtils.readFileFromZip(Path.of(zipFilePath), textFileName);
    // Assert
    assertEquals(textContent, result);
  }

  // Utilities
  public static Path createTempTextFile(String fileName, String content) throws IOException {
    Path tempFilePath = Paths.get(fileName);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFilePath.toFile()))) {
      writer.write(content);
    }
    return tempFilePath;
  }

  public static void createZipFile(String zipFilePath, Path fileToAdd, String entryName)
      throws IOException {
    try (ZipOutputStream zipOutputStream =
        new ZipOutputStream(new java.io.FileOutputStream(zipFilePath))) {
      // Add the file to the ZIP archive
      ZipEntry zipEntry = new ZipEntry(entryName);
      zipOutputStream.putNextEntry(zipEntry);

      // Write the file content to the ZIP archive
      byte[] bytes = java.nio.file.Files.readAllBytes(fileToAdd);
      zipOutputStream.write(bytes, 0, bytes.length);

      // Close the entry
      zipOutputStream.closeEntry();
    }
  }

  private enum Options {
    A,
    BC,
    D
  }
}
