package co.com.bancolombia.utils;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.resolver.DefaultResolver;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileUtilsTest {

    @Test
    public void readPropertiesExist() throws Exception {
        String property = "package";
        assertEquals("co.com.bancolombia", FileUtils.readProperties(property));
    }

    @Test(expected = IOException.class)
    public void readPropertiesNonExists() throws Exception {
        String property = "packageName";
        FileUtils.readProperties(property);
    }

    @Test
    public void readFile() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/resources")).build();
        String response = FileUtils.readFile(project, "temp.txt").collect(Collectors.joining());

        assertEquals("hello", response);
    }

    @Test
    public void readFileFromResources() throws IOException {
        DefaultResolver resolver = new DefaultResolver();
        String response = FileUtils.getResourceAsString(resolver, "temp.txt");

        assertEquals("hello", response);
    }

    @Test
    public void readYaml() throws IOException {
        File file = new File("src/test/resources/application.yaml");
        ObjectNode yaml = FileUtils.getFromYaml(file);

        assertEquals(8080, yaml.get("server").get("port").asInt());
    }

    @Test
    public void parseToYaml() throws IOException {
        File file = new File("src/test/resources/application.yaml");
        ObjectNode yaml = FileUtils.getFromYaml(file);
        ((ObjectNode) yaml.get("server")).put("port", 8081);
        String expected = "server:\n  port: 8081";

        String result = FileUtils.parseToYaml(yaml);

        assertEquals(expected, result.substring(0, expected.length()));
    }

    @Test
    public void writeString() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/tmp")).build();
        FileUtils.writeString(project, "temp.txt", "hello");
        String response = FileUtils.readFile(project, "temp.txt").collect(Collectors.joining());

        assertEquals("hello", response);
    }

    @Test
    public void finderSubProjects() {
        List<File> files = FileUtils.finderSubProjects("src/test/resources");

        assertEquals(0, files.size());

        List<File> files2 = FileUtils.finderSubProjects("src/test/resources/finderSubProjects/");

        assertEquals(2, files2.size());
    }

    // Assert
    @Test(expected = ParamNotFoundException.class)
    public void shouldHandleErrorWhenNotParamReplacePlaceholders() throws CleanException {
        // Arrange
        String fillablePath = "default/driven-adapters/{{name}}/src/main/{{className}}";
        Map<String, Object> params = new HashMap<>();
        params.put("className", "Redis.java");
        // Act
        Utils.fillPath(fillablePath, params);
    }

    @Test
    public void shouldExtractDir() {
        // Arrange
        String classPath = "default/driven-adapters/package/src/main/Model.java";
        // Act
        String result = Utils.extractDir(classPath);
        // Assert
        assertEquals("default/driven-adapters/package/src/main", result);
    }

    @Test
    public void shouldExtractDirWhenNoPath() {
        // Arrange
        String classPath = "Model.java";
        // Act
        String result = Utils.extractDir(classPath);
        // Assert
        assertNull(result);
    }

    @Test
    public void shouldFormatTaskOptions() {
        // Arrange
        List<?> options = List.of(Options.values());
        // Act
        String result = Utils.formatTaskOptions(options);
        // Assert
        assertEquals("[A|BC|D]", result);
    }

    @Test
    public void shouldFormatTaskOptionsSingle() {
        // Arrange
        List<?> options = List.of("A");
        // Act
        String result = Utils.formatTaskOptions(options);
        // Assert
        assertEquals("[A]", result);
    }

    @Test
    public void shouldAddDependency() {
        // Arrange
        String build = "apply plugin: 'org.springframework.boot'\n" +
                "\n" +
                "dependencies {\n" +
                "\timplementation project(':model')\n" +
                "\timplementation project(':usecase')\n" +
                "\tcompile 'org.springframework.boot:spring-boot-starter'\n" +
                "}";
        String expected = "apply plugin: 'org.springframework.boot'\n" +
                "\n" +
                "dependencies {\n" +
                "\timplementation project(':my-module')\n" +
                "\timplementation project(':model')\n" +
                "\timplementation project(':usecase')\n" +
                "\tcompile 'org.springframework.boot:spring-boot-starter'\n" +
                "}";
        // Act
        String result = Utils.addDependency(build, "implementation project(':my-module')");
        // Assert
        assertEquals(expected, result);
    }

    private enum Options {
        A, BC, D
    }

}
