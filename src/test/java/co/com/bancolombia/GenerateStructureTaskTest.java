package co.com.bancolombia;

import co.com.bancolombia.task.GenerateStructureTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class GenerateStructureTaskTest {

    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateStructureTask.class);

        GenerateStructureTask task = (GenerateStructureTask) project.getTasks().getByName("test");

        task.setPackage("test");
        task.setProjectName("projectTest");
        task.setType("imperative");
    }


}
