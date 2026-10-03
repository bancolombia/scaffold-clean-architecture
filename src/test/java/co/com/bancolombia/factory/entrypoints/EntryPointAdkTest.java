package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.TestUtils.assertFilesExists;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateEntryPointTask;
import co.com.bancolombia.task.GenerateStructureTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EntryPointAdkTest {
  private static final String TEST_DIR = getTestDir(EntryPointAdkTest.class);
  private static GenerateEntryPointTask task;

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(EntryPointAdkTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateEntryPointTask.class);
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldGenerateAdkEntryPoint() throws IOException, CleanException {
    task.setType("ADK");
    task.execute();

    assertFilesExists(
        TEST_DIR + "/infrastructure/entry-points/adk-agent/build.gradle",
        TEST_DIR
            + "/infrastructure/entry-points/adk-agent/src/main/java/co/com/bancolombia/adk/config/AdkAgentConfig.java",
        TEST_DIR
            + "/infrastructure/entry-points/adk-agent/src/main/java/co/com/bancolombia/adk/tools/GreetingTool.java",
        TEST_DIR
            + "/infrastructure/entry-points/adk-agent/src/main/java/co/com/bancolombia/adk/endpoint/AdkChatEndpoint.java",
        TEST_DIR + "/domain/model/src/main/java/co/com/bancolombia/model/adk/AdkChatRequest.java",
        TEST_DIR + "/domain/model/src/main/java/co/com/bancolombia/model/adk/AdkChatResponse.java",
        TEST_DIR
            + "/domain/model/src/main/java/co/com/bancolombia/model/adk/gateways/AdkAgentGateway.java",
        TEST_DIR
            + "/domain/usecase/src/main/java/co/com/bancolombia/usecase/adk/AdkChatUseCase.java");
  }
}
