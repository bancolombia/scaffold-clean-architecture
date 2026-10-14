package co.com.bancolombia.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DependencyOverrideConventionsTest {

  @Test
  void shouldApplyExactAndGroupOverridesToAllProjects(@TempDir Path mavenRepo) throws IOException {
    Project root = ProjectBuilder.builder().withName("root").build();
    Project child = ProjectBuilder.builder().withName("app-service").withParent(root).build();
    new DependencyOverrideConventions().configure(root);

    DependenciesOverrideExtension extension =
        root.getExtensions().getByType(DependenciesOverrideExtension.class);
    extension.ensure("com.fasterxml.jackson.core:jackson-core:2.22.3");
    extension.ensure("tools.jackson.core:3.2.3");

    addMavenRepository(root, mavenRepo);
    addMavenRepository(child, mavenRepo);
    writePom(mavenRepo, "com.fasterxml.jackson.core", "jackson-core", "2.15.0");
    writePom(mavenRepo, "com.fasterxml.jackson.core", "jackson-core", "2.22.3");
    writePom(mavenRepo, "tools.jackson.core", "jackson-databind", "3.0.0");
    writePom(mavenRepo, "tools.jackson.core", "jackson-databind", "3.2.3");

    assertEquals("2.22.3", resolveVersion(root, "com.fasterxml.jackson.core:jackson-core:2.15.0"));
    assertEquals("3.2.3", resolveVersion(child, "tools.jackson.core:jackson-databind:3.0.0"));
  }

  private void addMavenRepository(Project project, Path mavenRepo) {
    project.getRepositories().maven(repository -> repository.setUrl(mavenRepo.toUri()));
  }

  private void writePom(Path mavenRepo, String group, String name, String version)
      throws IOException {
    Path pom =
        mavenRepo
            .resolve(group.replace('.', '/'))
            .resolve(name)
            .resolve(version)
            .resolve(name + "-" + version + ".pom");
    Files.createDirectories(pom.getParent());
    Files.writeString(
        pom,
        """
        <project xmlns="http://maven.apache.org/POM/4.0.0">
          <modelVersion>4.0.0</modelVersion>
          <groupId>%s</groupId>
          <artifactId>%s</artifactId>
          <version>%s</version>
        </project>
        """
            .formatted(group, name, version));
  }

  private String resolveVersion(Project project, String dependencyNotation) {
    String[] coordinates = dependencyNotation.split(":");
    Configuration configuration = project.getConfigurations().create("dependencyOverrideCheck");
    project.getDependencies().add(configuration.getName(), dependencyNotation);
    return configuration.getIncoming().getResolutionResult().getAllComponents().stream()
        .map(ResolvedComponentResult::getModuleVersion)
        .filter(Objects::nonNull)
        .filter(
            moduleVersion ->
                moduleVersion.getGroup().equals(coordinates[0])
                    && moduleVersion.getName().equals(coordinates[1]))
        .map(ModuleVersionIdentifier::getVersion)
        .findFirst()
        .orElseThrow();
  }
}
