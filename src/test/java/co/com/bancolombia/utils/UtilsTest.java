package co.com.bancolombia.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.IOException;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

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
    List<?> options = Arrays.asList(Options.values());
    // Act
    String result = Utils.formatTaskOptions(options);
    // Assert
    assertEquals("[A|BC|D]", result);
  }

  @Test
  public void shouldFormatTaskOptionsSingle() {
    // Arrange
    List<?> options = Collections.singletonList("A");
    // Act
    String result = Utils.formatTaskOptions(options);
    // Assert
    assertEquals("[A]", result);
  }

  @Test
  public void shouldAddDependency() {
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
  public void shouldNotAddDependencyWhenExists() {
    // Arrange
    String build =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':my-module')\n"
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
  public void shouldAddConfigurationAndConfigurationsBlock() {
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
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}\n"
            + "\n"
            + "configurations{\n"
            + "\tcompile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"\n"
            + "}";
    // Act
    String result =
        Utils.addConfiguration(
            build,
            "compile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"");
    // Assert
    assertEquals(expected, result);
  }

  @Test
  public void shouldAddConfiguration() {
    // Arrange
    String build =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}\n"
            + "\n"
            + "configurations{\n"
            + "\tcompile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"\n"
            + "}";
    String expected =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}\n"
            + "\n"
            + "configurations{\n"
            + "\tcompile.exclude group: \"co.com.bancolombia\", module:\"excluded-module\"\n"
            + "\tcompile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"\n"
            + "}";
    // Act
    String result =
        Utils.addConfiguration(
            build, "compile.exclude group: \"co.com.bancolombia\", module:\"excluded-module\"");
    // Assert
    assertEquals(expected, result);
  }

  @Test
  public void shouldNotAddConfigurationWhenExists() {
    // Arrange
    String build =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}\n"
            + "\n"
            + "configurations{\n"
            + "\tcompile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"\n"
            + "}";
    String expected =
        "apply plugin: 'org.springframework.boot'\n"
            + "\n"
            + "dependencies {\n"
            + "\timplementation project(':model')\n"
            + "\timplementation project(':usecase')\n"
            + "\timplementation 'org.springframework.boot:spring-boot-starter'\n"
            + "}\n"
            + "\n"
            + "configurations{\n"
            + "\tcompile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"\n"
            + "}";
    // Act
    String result =
        Utils.addConfiguration(
            build,
            "compile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"");
    // Assert
    assertEquals(expected, result);
  }

  @Test
  public void shouldGenerateDashName() {
    String res = Utils.toDashName("MyCamelCase");
    assertEquals("my-camel-case", res);
  }

  @Test
  public void shouldGenerateDashNameNonStartsWithUpper() {
    String res = Utils.toDashName("myCamelCase");
    assertEquals("my-camel-case", res);
  }

  @Test
  public void shouldAddModule() {
    // Arrange
    String settings =
        "rootProject.name = 'cleanArchitecture'\n"
            + "\n"
            + "include ':app-service'\n"
            + "include ':model'\n"
            + "include ':usecase'\n"
            + "project(':app-service').projectDir = file('./applications/app-service')\n"
            + "project(':model').projectDir = file('./domain/model')\n"
            + "project(':usecase').projectDir = file('./domain/usecase')\n"
            + "include ':api-rest'\n"
            + "project(':api-rest').projectDir = file('./infrastructure/entry-points/api-rest')";
    String settingsNew =
        "rootProject.name = 'cleanArchitecture'\n"
            + "\n"
            + "include ':app-service'\n"
            + "include ':model'\n"
            + "include ':usecase'\n"
            + "project(':app-service').projectDir = file('./applications/app-service')\n"
            + "project(':model').projectDir = file('./domain/model')\n"
            + "project(':usecase').projectDir = file('./domain/usecase')\n"
            + "include ':api-rest'\n"
            + "project(':api-rest').projectDir = file('./infrastructure/entry-points/api-rest')\n"
            + "include ':my-module'\n"
            + "project(':my-module').projectDir = file('./infrastructure/entry-points/my-module')";
    // Act
    String result =
        Utils.addModule(
            settings, Utils.INCLUDE_MODULE_JAVA, "my-module", "infrastructure/entry-points");
    // Assert
    assertEquals(settingsNew, result);
  }

  @Test
  public void shouldNotAddRepeatedModule() {
    // Arrange
    String settings =
        "rootProject.name = 'cleanArchitecture'\n"
            + "\n"
            + "include ':app-service'\n"
            + "include ':model'\n"
            + "include ':usecase'\n"
            + "project(':app-service').projectDir = file('./applications/app-service')\n"
            + "project(':model').projectDir = file('./domain/model')\n"
            + "project(':usecase').projectDir = file('./domain/usecase')\n"
            + "include ':api-rest'\n"
            + "project(':api-rest').projectDir = file('./infrastructure/entry-points/api-rest')\n"
            + "include ':my-module'\n"
            + "project(':my-module').projectDir = file('./infrastructure/entry-points/my-module')";
    // Act
    String result =
        Utils.addModule(
            settings, Utils.INCLUDE_MODULE_JAVA, "my-module", "infrastructure/entry-points");
    // Assert
    assertEquals(settings, result);
  }

  @Test
  public void shouldRemoveModule() {
    // Arrange
    String settings =
        "rootProject.name = 'cleanArchitecture'\n"
            + "\n"
            + "include ':app-service'\n"
            + "include ':model'\n"
            + "include ':usecase'\n"
            + "project(':app-service').projectDir = file('./applications/app-service')\n"
            + "project(':model').projectDir = file('./domain/model')\n"
            + "project(':usecase').projectDir = file('./domain/usecase')\n"
            + "include ':api-rest'\n"
            + "project(':api-rest').projectDir = file('./infrastructure/entry-points/api-rest')\n"
            + "include ':my-module'\n"
            + "project(':my-module').projectDir = file('./infrastructure/entry-points/my-module')";
    String settingsExpected =
        "rootProject.name = 'cleanArchitecture'\n"
            + "\n"
            + "include ':app-service'\n"
            + "include ':model'\n"
            + "include ':usecase'\n"
            + "project(':app-service').projectDir = file('./applications/app-service')\n"
            + "project(':model').projectDir = file('./domain/model')\n"
            + "project(':usecase').projectDir = file('./domain/usecase')\n"
            + "include ':my-module'\n"
            + "project(':my-module').projectDir = file('./infrastructure/entry-points/my-module')";
    // Act
    String result = Utils.removeLinesIncludes(settings, "api-rest");
    // Assert
    assertEquals(settingsExpected, result);
  }

  @Test
  public void ShouldGetBuildImplementation() {
    String result = Utils.buildImplementation(true, "module");
    assertEquals("implementation(\"module\")", result);
    result = Utils.buildImplementation(false, "module");
    assertEquals("implementation 'module'", result);
  }

  @Test
  public void shouldReplaceExpression() {
    String expected = "'com.fasterxml.jackson.core:jackson-databind:2.11.0'";
    String result =
        Utils.replaceExpression(
            "'com.github.spullara.mustache.java:compiler:0.9.6'",
            "['\"]com.github.spullara.mustache.java:compiler:[^\\$].+",
            expected);
    assertEquals(expected, result);
  }

  @Test
  public void shouldGetAllFilesWithExtension() throws IOException {
    List<String> result = Utils.getAllFilesWithExtension(true);
    assertEquals(true, result.stream().allMatch(s -> s.endsWith("gradle.kts")));
    result = Utils.getAllFilesWithExtension(false);
    assertEquals(true, result.stream().allMatch(s -> s.endsWith("gradle")));
  }

  @Test
  public void sample() throws IOException {
    assertEquals("my/path/package", "my.path.package".replace('.', '/'));
    assertEquals("mypathpackage", "my'path'package".replace("'", ""));
    assertEquals("mypathpackage", "my\"path\"package".replace("\"", ""));
  }

  @Test
  public void shouldReplaceGroup() throws IOException {
    String regex =
        "(Get|Post|Put|Delete|Patch|Request)Mapping\\((.*(path|value)\\s*=\\s*|)\\\".*(/)\\\".*\\)";
    DefaultResolver resolver = new DefaultResolver();
    String text = FileUtils.getResourceAsString(resolver, "rest/mapping-before.txt");
    String expectedText = FileUtils.getResourceAsString(resolver, "rest/mapping-after.txt");

    String result = Utils.replaceGroup(text, regex, "", 4);
    assertEquals(expectedText, result);
  }

  private enum Options {
    A,
    BC,
    D
  }
}
