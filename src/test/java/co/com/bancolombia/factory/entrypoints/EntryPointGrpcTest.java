package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.TestUtils.assertFileContains;
import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntryPointGrpcTest {
  private static final String TEST_DIR = getTestDir(EntryPointGrpcTest.class);

  private GenerateEntryPointTask task;

  @BeforeEach
  void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(EntryPointGrpcTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateEntryPointTask.class);
  }

  @AfterEach
  void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldGenerateGrpcEntryPoint() throws IOException, CleanException {
    // Arrange
    task.setType("GRPC");

    // Act
    task.execute();

    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/grpc/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/grpc/GrpcServerEntryPoint.java",
        "src/main/java/co/com/bancolombia/grpc/mapper/ErrorMapper.java",
        "src/main/proto/service.proto",
        "src/test/java/co/com/bancolombia/grpc/GrpcServerEntryPointTest.java");
  }

  @Test
  void shouldGenerateBuildGradleWithProtobufPlugin() throws IOException, CleanException {
    // Arrange
    task.setType("GRPC");

    // Act
    task.execute();

    // Assert
    assertFileContains(
        TEST_DIR + "/infrastructure/entry-points/grpc/build.gradle",
        "com.google.protobuf",
        "grpc-server-spring-boot-starter",
        "grpc-netty-shaded",
        "grpc-protobuf",
        "grpc-stub",
        "protobuf {");
  }

  @Test
  void shouldGenerateProtoFile() throws IOException, CleanException {
    // Arrange
    task.setType("GRPC");

    // Act
    task.execute();

    // Assert
    assertFileContains(
        TEST_DIR + "/infrastructure/entry-points/grpc/src/main/proto/service.proto",
        "syntax = \"proto3\"",
        "service CustomService",
        "rpc getById",
        "rpc health");
  }

  @Test
  void shouldAddGrpcServerPortToProperties() throws IOException, CleanException {
    // Arrange
    task.setType("GRPC");

    // Act
    task.execute();

    // Assert
    assertFileContains(
        TEST_DIR + "/applications/app-service/src/main/resources/application.yaml", "grpc:");
  }

  @Test
  void shouldAddDependencyToAppService() throws IOException, CleanException {
    // Arrange
    task.setType("GRPC");

    // Act
    task.execute();

    // Assert
    assertFileContains(
        TEST_DIR + "/applications/app-service/build.gradle", "implementation project(':grpc')");
  }

  @Test
  void shouldAddModuleToSettings() throws IOException, CleanException {
    // Arrange
    task.setType("GRPC");

    // Act
    task.execute();

    // Assert
    assertFileContains(TEST_DIR + "/settings.gradle", "grpc");
  }
}
