package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertNull;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class UpdateProjectTaskTest {
  private UpdateProjectTask task;

  @Before
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("test", UpdateProjectTask.class);
    task = (UpdateProjectTask) project.getTasks().getByName("test");
    task.setGit(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
  }

  @Test
  public void shouldUpdateProject() throws IOException, CleanException {
    task.execute();
    assertNull(task.getState().getOutcome());
  }

  @Test
  public void shouldUpdateProjectAndSomeDependencies() throws IOException, CleanException {
    // Arrange
    task.setDependencies("org.mockito:mockito-core org.projectlombok:lombok");
    // Act
    task.execute();
    assertNull(task.getState().getOutcome());
  }

  @Test
  public void shouldNotUpdateProjectAndSomeDependencies() throws IOException, CleanException {
    // Arrange
    task.setDependencies("does_dependency:not_exist");
    // Act
    task.execute();
    assertNull(task.getState().getOutcome());
  }

  @Test
  public void dependencyIncomplete() throws IOException, CleanException {
    // Arrange
    task.setDependencies("does_dependency");
    // Act

    task.execute();
    assertNull(task.getState().getOutcome());
  }
}
