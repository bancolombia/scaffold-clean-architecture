package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.task.GenerateStructureTask;
import co.com.bancolombia.utils.FileUtils;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntryPointMcpTest {
  private static final String TEST_DIR = getTestDir(EntryPointMcpTest.class);

  private ModuleBuilder builder;
  private EntryPointMcp entryPointMcp;

  @BeforeEach
  void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(EntryPointMcpTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    builder = new ModuleBuilder(project);
    builder.addParam("mcp-enable-tools", true);
    builder.addParam("mcp-enable-resources", false);
    builder.addParam("mcp-enable-prompts", false);
    builder.addParam("mcp-enable-security", false);
    entryPointMcp = new EntryPointMcp();
  }

  @AfterEach
  void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldAddParametersFlagBlockWhenAppBuildContainsCompilerArgsBlock()
      throws IOException, CleanException {
    String appBuild = Files.readString(Path.of(TEST_DIR, APP_BUILD_GRADLE));
    appBuild +=
        """
                        tasks.withType(JavaCompile).configureEach {
                            options.compilerArgs = ['-Amapstruct.suppressGeneratorTimestamp=true']
                        }

                        """;
    assertTrue(appBuild.contains("options.compilerArgs = ["));
    builder.addFile(APP_BUILD_GRADLE, appBuild);

    entryPointMcp.buildModule(builder);

    String updated = readGeneratedAppBuildGradle();
    assertTrue(updated.contains("options.compilerArgs = ["));
    assertTrue(updated.contains("if (!options.compilerArgs.contains('-parameters')) {"));
    assertEquals(1, countOccurrences(updated, "options.compilerArgs += '-parameters'"));
  }

  @Test
  void shouldNotDuplicateParametersBlockWhenAlreadyPresent() throws IOException, CleanException {
    String appBuild =
        """
                        plugins { id 'org.springframework.boot' }
                        dependencies { implementation 'org.springframework.boot:spring-boot-starter' }
                          tasks.withType(JavaCompile).configureEach {
                              options.compilerArgs = [
                                      '-Amapstruct.suppressGeneratorTimestamp=true'
                              ]
                              doFirst {
                                  if (!options.compilerArgs.contains('-parameters')) {
                                      options.compilerArgs += '-parameters'
                                  }
                              }
                          }
                        """;
    builder.addFile(APP_BUILD_GRADLE, appBuild);

    entryPointMcp.buildModule(builder);

    String updated = readGeneratedAppBuildGradle();
    assertEquals(1, countOccurrences(updated, "doFirst {"));
    assertEquals(1, countOccurrences(updated, "options.compilerArgs += '-parameters'"));
  }

  @Test
  void shouldKeepWorkingWhenAppBuildContainsJavaBlock() throws IOException, CleanException {
    DefaultResolver resolver = new DefaultResolver();
    builder.addFile(
        APP_BUILD_GRADLE,
        FileUtils.getResourceAsString(resolver, "gradle-8.11-java-block/main-after.txt"));

    entryPointMcp.buildModule(builder);

    String updated = readGeneratedAppBuildGradle();
    assertTrue(updated.contains("java {"));
    assertTrue(updated.contains("tasks.withType(JavaCompile).configureEach {"));
    assertTrue(updated.contains("if (!options.compilerArgs.contains('-parameters')) {"));
    assertEquals(1, countOccurrences(updated, "options.compilerArgs += '-parameters'"));
  }

  private String readGeneratedAppBuildGradle() throws IOException {
    AtomicReference<String> content = new AtomicReference<>();
    builder.updateFile(
        APP_BUILD_GRADLE,
        current -> {
          content.set(current);
          return current;
        });
    return content.get();
  }

  private int countOccurrences(String content, String value) {
    int count = 0;
    int fromIndex = 0;
    while ((fromIndex = content.indexOf(value, fromIndex)) >= 0) {
      count++;
      fromIndex += value.length();
    }
    return count;
  }
}
