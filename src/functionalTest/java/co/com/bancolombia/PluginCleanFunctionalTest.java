/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package co.com.bancolombia;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A simple functional test for the 'co.com.bancolombia.greeting' plugin.
 */

public class PluginCleanFunctionalTest {
    File projectDir = new File("build/functionalTest");
    GradleRunner runner;

    @Before
    public void init() throws IOException {
        // Setup the test build
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");
        runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
    }

    @Test
    public void canRunTaskGenerateStructureWithOutParameters() {

        String task = "ca";

        runner.withArguments(task);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();
        // Verify the result
        assertTrue(new File("build/functionalTest/README.md").exists());
        assertTrue(new File("build/functionalTest/.gitignore").exists());
        assertTrue(new File("build/functionalTest/build.gradle").exists());
        assertTrue(new File("build/functionalTest/lombok.config").exists());
        assertTrue(new File("build/functionalTest/main.gradle").exists());
        assertTrue(new File("build/functionalTest/settings.gradle").exists());

        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/").exists());
        assertTrue(new File("build/functionalTest/infrastructure/entry-points").exists());
        assertTrue(new File("build/functionalTest/infrastructure/helpers").exists());

        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/bancolombia/model").exists());
        assertTrue(new File("build/functionalTest/domain/model/src/test/java/co/com/bancolombia/model").exists());
        assertTrue(new File("build/functionalTest/domain/model/build.gradle").exists());
        assertTrue(new File("build/functionalTest/domain/usecase/src/main/java/co/com/bancolombia/usecase").exists());
        assertTrue(new File("build/functionalTest/domain/usecase/src/test/java/co/com/bancolombia/usecase").exists());
        assertTrue(new File("build/functionalTest/domain/usecase/build.gradle").exists());

        assertTrue(new File("build/functionalTest/applications/app-service/build.gradle").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/java/co/com/bancolombia/MainApplication.java").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/java/co/com/bancolombia/config").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/resources/application.yaml").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/resources/log4j2.properties").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/test/java/co/com/bancolombia").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }


    @Test
    public void canRunTaskGenerateStructureWithParameters() {
        String task = "cleanArchitecture";
        String packageName = "co.com.test";
        String projectName = "ProjectName";

        runner.withArguments(task, "--name=" + projectName, "--package=" + packageName);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();
        // Verify the result

        assertTrue(new File("build/functionalTest/README.md").exists());
        assertTrue(new File("build/functionalTest/.gitignore").exists());
        assertTrue(new File("build/functionalTest/build.gradle").exists());
        assertTrue(new File("build/functionalTest/lombok.config").exists());
        assertTrue(new File("build/functionalTest/main.gradle").exists());
        assertTrue(new File("build/functionalTest/settings.gradle").exists());

        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/").exists());
        assertTrue(new File("build/functionalTest/infrastructure/entry-points").exists());
        assertTrue(new File("build/functionalTest/infrastructure/helpers").exists());

        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/test/model").exists());
        assertTrue(new File("build/functionalTest/domain/model/src/test/java/co/com/test/model").exists());
        assertTrue(new File("build/functionalTest/domain/model/build.gradle").exists());
        assertTrue(new File("build/functionalTest/domain/usecase/src/main/java/co/com/test/usecase").exists());
        assertTrue(new File("build/functionalTest/domain/usecase/src/test/java/co/com/test/usecase").exists());
        assertTrue(new File("build/functionalTest/domain/usecase/build.gradle").exists());

        assertTrue(new File("build/functionalTest/applications/app-service/build.gradle").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/java/co/com/test/MainApplication.java").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/java/co/com/test/config").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/resources/application.yaml").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/main/resources/log4j2.properties").exists());
        assertTrue(new File("build/functionalTest/applications/app-service/src/test/java/co/com/test").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskGenerateModelWithParameters() {
        String task = "generateModel";
        String modelName = "testModel";

        // Run the build
        runner.withArguments(task, "--name=" + modelName);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();
        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/bancolombia/model/testmodel/gateways/TestModelRepository.java").exists());
        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/bancolombia/model/testmodel/TestModel.java").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskGenerateUseCaseWithParameters() {
        canRunTaskGenerateStructureWithOutParameters();
        String task = "generateUseCase";
        String useCaseName = "business";

        // Setup the test buildº
        runner.withArguments(task, "--name=" + useCaseName);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();
        assertTrue(new File("build/functionalTest/domain/usecase/src/main/java/co/com/bancolombia/usecase/business/BusinessUseCase.java").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskGenerateEntryPointCaseWithParameters() {
        canRunTaskGenerateStructureWithOutParameters();
        String task = "generateEntryPoint";
        String valueEntryPoint = "restmvc";

        runner.withArguments(task, "--type=" + valueEntryPoint);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();
        assertTrue(new File("build/functionalTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java").exists());
        assertTrue(new File("build/functionalTest/infrastructure/entry-points/api-rest/build.gradle").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskGenerateDrivenAdapterWithParameters() {
        canRunTaskGenerateStructureWithOutParameters();
        String task = "generateDrivenAdapter";
        String valueDrivenAdapter = "jpa";

        runner.withArguments(task, "--type=" + valueDrivenAdapter);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/jpa-repository/build.gradle").exists());
        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepository.java").exists());
        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepositoryAdapter.java").exists());

        assertTrue(new File("build/functionalTest/infrastructure//helpers/jpa-repository-commons/build.gradle").exists());
        assertTrue(new File("build/functionalTest/infrastructure/helpers/jpa-repository-commons/src/main/java/co/com/bancolombia/jpa/AdapterOperations.java").exists());

        // TODO: Enable test
//        assertTrue(new File("build/functionalTest/applications/app-service/src/main/java/co/com/bancolombia/config/jpa/JpaConfig.java").exists());
//        assertTrue(new File("build/functionalTest/applications/app-service/src/main/resources/application-jpaAdapter.yaml").exists());
//        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/bancolombia/model/secret/Secret.java").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskGenerateDrivenAdapterEventBusTest() {
        canRunTaskGenerateStructureWithOutParameters();
        String task = "generateDrivenAdapter";
        String valueDrivenAdapter = "ASYNCEVENTBUS";

        runner.withArguments(task, "--type=" + valueDrivenAdapter);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/async-event-bus/build.gradle").exists());
        assertTrue(new File("build/functionalTest/infrastructure/driven-adapters/async-event-bus/src/main/java/co/com/bancolombia/event/ReactiveEventsGateway.java").exists());
        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/bancolombia/model/event/gateways/EventsGateway.java").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskGeneratePipelineAzureDevOpsTest() {
        canRunTaskGenerateStructureWithOutParameters();
        String task = "generatePipeline";
        String valuePipeline = "AZURE";

        runner.withArguments(task, "--type=" + valuePipeline);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        assertTrue(new File("build/functionalTest/deployment/cleanarchitecture_azure_build.yaml").exists());

        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void canRunTaskvalidateStructureWithOutParameters() {
        canRunTaskGenerateStructureWithOutParameters();
        String task = "validateStructure";

        runner.withArguments(task);
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        // Verify the result
        assertEquals(result.task(":" + task).getOutcome(), TaskOutcome.SUCCESS);
    }

    @Test
    public void createTasks() {

        runner.withArguments("tasks");
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        // Verify the result
        assertTrue(result.getOutput().contains("cleanArchitecture"));
        assertTrue(result.getOutput().contains("generateModel"));
        assertTrue(result.getOutput().contains("validateStructure"));

        assertEquals(result.task(":tasks").getOutcome(), TaskOutcome.SUCCESS);
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }


}
