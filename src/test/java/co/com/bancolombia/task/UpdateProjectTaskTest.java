package co.com.bancolombia.task;

import static org.junit.Assert.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class UpdateProjectTaskTest {
  private UpdateProjectTask task;

  @Before
  public void setup() throws IOException, CleanException {
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.generateStructureTask();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("test", UpdateProjectTask.class);
    task = (UpdateProjectTask) project.getTasks().getByName("test");
  }

  @Test
  public void shouldUpdateProject() throws IOException {
    task.updateProject();
  }

  @Test
  public void shouldUpdateProjectAndSomeDependencies() throws IOException {
    // Arrange
    task.setDependencies("org.mockito:mockito-core org.projectlombok:lombok");
    // Act
    task.updateProject();
  }

  @Test
  public void shouldNotUpdateProjectAndSomeDependencies() throws IOException {
    // Arrange
    task.setDependencies("does_dependency:not_exist");
    // Act
    task.updateProject();
  }

  @Test
  public void dependencyIncomplete() {
    // Arrange
    task.setDependencies("does_dependency");
    // Act

    assertThrows(IllegalArgumentException.class, () -> task.updateProject());
  }
}
