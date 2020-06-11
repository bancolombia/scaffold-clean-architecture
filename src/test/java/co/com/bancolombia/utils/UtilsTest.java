package co.com.bancolombia.utils;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsTest {

    @Test
    public void getVersionPlugin() {
        Assert.assertEquals(Constants.PLUGIN_VERSION, Utils.getVersionPlugin());
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
