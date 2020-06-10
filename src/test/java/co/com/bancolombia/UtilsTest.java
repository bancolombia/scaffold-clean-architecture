package co.com.bancolombia;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.templates.PluginTemplate;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsTest {


    @Test
    public void getVersionPlugin() {
        Assert.assertEquals(PluginTemplate.VERSION_PLUGIN, Utils.getVersionPlugin());
    }

    @Test
    public void capitalize() {
        String test1 = "capitalize";
        String test2 = "capitalizeTest";

        Assert.assertEquals("Capitalize", Utils.capitalize(test1));
        Assert.assertEquals("CapitalizeTest", Utils.capitalize(test2));
    }

    @Test
    public void decapitalize() {
        String test1 = "Decapitalize";
        String test2 = "DecapitalizeTest";
        String test3 = "DECAPITALIZE";
        Assert.assertEquals("decapitalize", Utils.decapitalize(test1));
        Assert.assertEquals("decapitalizeTest", Utils.decapitalize(test2));
        Assert.assertEquals("dECAPITALIZE", Utils.decapitalize(test3));
    }

    @Test
    public void readPropertiesExist() throws Exception {
        String test1 = "package";
        Assert.assertEquals("co.com.bancolombia", FileUtils.readProperties(test1));

    }

    @Test(expected = Exception.class)
    public void readPropertiesUnExist() throws Exception {
        String test1 = "package2";
        FileUtils.readProperties(test1);

    }

    @Test
    public void readFile() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/resources")).build();
        String response = FileUtils.readFile(project, "temp.txt").collect(Collectors.joining());

        Assert.assertTrue(response instanceof String);
        Assert.assertEquals("hello", response);
    }

    @Test
    public void writeString() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/tmp")).build();
        FileUtils.writeString(project, "temp.txt", "hello");
        String response = FileUtils.readFile(project, "temp.txt").collect(Collectors.joining());

        Assert.assertTrue(response instanceof String);
        Assert.assertEquals("hello", response);
    }

    @Test
    public void finderSubProjects() {
        List<File> files = FileUtils.finderSubProjects("src/test/resources");

        Assert.assertEquals(0, files.size());

        List<File> files2 = FileUtils.finderSubProjects("src/test/resources/finderSubProjects/");

        Assert.assertEquals(2, files2.size());
    }

    @Test
    public void shouldJoinPath() {
        String expected = "a/b/c/d";
        String result = Utils.joinPath("a", "b", "c", "d");
        Assert.assertEquals(expected, result);
    }


    @Test
    public void shouldReplacePlaceholders() throws CleanException {
        // Arrange
        String fillablePath = "default/driven-adapters/{{name}}/src/main/{{className}}";
        // Act
        Map<String, Object> params = new HashMap<>();
        params.put("name", "redis");
        params.put("className", "Redis.java");
        String result = Utils.fillPath(fillablePath, params);
        // Assert
        assertEquals("default/driven-adapters/redis/src/main/Redis.java", result);
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
    public void shouldExtractDir() throws CleanException {
        // Arrange
        String classPath = "default/driven-adapters/package/src/main/Model.java";
        // Act
        String result = Utils.extractDir(classPath);
        // Assert
        assertEquals("default/driven-adapters/package/src/main", result);
    }

    @Test
    public void shouldExtractDirWhenNoPath() throws CleanException {
        // Arrange
        String classPath = "Model.java";
        // Act
        String result = Utils.extractDir(classPath);
        // Assert
        assertNull(result);
    }

}
