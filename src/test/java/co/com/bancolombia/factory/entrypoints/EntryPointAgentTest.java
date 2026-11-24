package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.TemplateDefinition;
import co.com.bancolombia.task.GenerateEntryPointTask;
import co.com.bancolombia.task.GenerateStructureTask;
import co.com.bancolombia.utils.FileUtils;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class EntryPointAgentTest {
  private static final String TEST_DIR = getTestDir(EntryPointAgentTest.class);

  private Project project;

  @BeforeEach
  void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    project = setupProject(EntryPointAgentTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();
  }

  @AfterEach
  void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldNotHaveOrphanTemplatesInMcpClient() throws IOException {
    DefaultResolver resolver = new DefaultResolver();
    ObjectMapper mapper = new ObjectMapper();
    TemplateDefinition definition =
        FileUtils.loadTemplateDefinition(
            resolver,
            mapper,
            "entry-point/agent/mcp-client",
            "definition.json",
            TemplateDefinition.class);
    assertNotNull(definition);
    Set<String> declaredFiles = definition.getFiles().keySet();

    Path mcpClientDir = Path.of("src/main/resources/entry-point/agent/mcp-client");
    try (Stream<Path> files = Files.list(mcpClientDir)) {
      List<String> mustacheFiles =
          files
              .filter(p -> p.getFileName().toString().endsWith(".mustache"))
              .map(p -> "entry-point/agent/mcp-client/" + p.getFileName().toString())
              .toList();

      for (String mustacheFile : mustacheFiles) {
        assertTrue(
            declaredFiles.contains(mustacheFile),
            () -> "Orphan template found: " + mustacheFile + " is not declared in definition.json");
      }
    }

    assertFalse(
        Files.exists(mcpClientDir.resolve("security-config.java.mustache")),
        "security-config.java.mustache should not exist");
    assertFalse(
        Files.exists(mcpClientDir.resolve("oauth2-web-client-config.java.mustache")),
        "oauth2-web-client-config.java.mustache should not exist");
  }

  @Test
  void shouldGenerateAgentEntryPointCollaborative() throws IOException, CleanException {
    GenerateEntryPointTask task = createTask(project, GenerateEntryPointTask.class);
    task.setType("agent");
    task.setAgentRole("collaborative");
    task.execute();

    assertFilesExistsInDir(
        TEST_DIR,
        "infrastructure/driven-adapters/mcp-client/build.gradle",
        "infrastructure/driven-adapters/mcp-client/src/main/java/co/com/bancolombia/mcpclient/adapter/ChatGatewayAdapter.java",
        "infrastructure/driven-adapters/mcp-client/src/test/java/co/com/bancolombia/mcpclient/adapter/ChatGatewayAdapterTest.java");

    assertFalse(
        Files.exists(
            Path.of(
                TEST_DIR,
                "infrastructure/driven-adapters/mcp-client/src/main/java/co/com/bancolombia/mcpclient/adapter/config/SecurityConfig.java")));
  }
}
