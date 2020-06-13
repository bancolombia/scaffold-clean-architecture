package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.pipelines.ModuleFactoryPipeline;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class GeneratePipelineTaskTest {

    GeneratePipelineTask task;

    @Before
    public void init() throws IOException, CleanException {
        File projectDir = new File("build/unitTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");

        Project project = ProjectBuilder.builder().withName("cleanArchitecture")
                .withProjectDir(new File("build/unitTest")).build();

        project.getTasks().create("testStructure", GenerateStructureTask.class);
        GenerateStructureTask taskStructure = (GenerateStructureTask) project.getTasks().getByName("testStructure");
        taskStructure.generateStructureTask();

        project.getTasks().create("test", GeneratePipelineTask.class);
        task = (GeneratePipelineTask) project.getTasks().getByName("test");
    }

    @Test
    public void generateAzureDevOpsPipelineTest() throws IOException, CleanException {

        task.setType(ModuleFactoryPipeline.PipelineType.AZURE);
        task.generatePipelineTask();

        assertTrue(new File("build/unitTest/deployment/cleanarchitecture_azure_build.yaml").exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateAzureDevOpsPipelineErrorTest() throws IOException, CleanException {
        task.setType(null);
        task.generatePipelineTask();
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
