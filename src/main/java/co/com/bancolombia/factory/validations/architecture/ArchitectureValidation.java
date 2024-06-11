package co.com.bancolombia.factory.validations.architecture;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.FileUtils;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedDependency;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArchitectureValidation {

  public static void inject(Project project, ModuleBuilder builder) {
    if (!FileUtils.readBooleanProperty("skipArchitectureTests")) {
      String os = System.getProperty("os.name");
      String paths =
          project.getAllprojects().stream()
              .map(p -> "\"" + toOSPath(os, p.getProjectDir()) + "/\"")
              .collect(Collectors.joining(","));
      builder.addParam("reactive", builder.isReactive());
      builder.addParam("modulePaths", paths);
      project.getAllprojects().stream()
          .filter(p -> p.getName().equals(Constants.APP_SERVICE))
          .findFirst()
          .ifPresent(appService -> generateArchUnitFiles(project, appService, builder));
    }
  }

  private static String toOSPath(String os, File projectDir) {
    if (os != null && os.contains("Windows")) {
      return projectDir.toString().replace("\\", "\\\\");
    }
    return projectDir.toString();
  }

  private static void prepareParams(Project project, Project appService, ModuleBuilder builder) {
    Stream<DeclaredDependency> deps =
        appService.getConfigurations().stream()
            .filter(Configuration::isCanBeResolved)
            .flatMap(c -> c.getResolvedConfiguration().getFirstLevelModuleDependencies().stream())
            .map(DeclaredDependency::new);

    boolean hasSpringWeb =
        deps.flatMap(DeclaredDependency::flattened)
            .anyMatch(
                dep ->
                    dep != null
                        && dep.name != null
                        && dep.group != null
                        && dep.group.equals("org.springframework")
                        && dep.name.equals("spring-web"));

    project.getLogger().debug("hasSpringWeb: {}", hasSpringWeb);
    builder.addParam("hasSpringWeb", hasSpringWeb);
  }

  @SneakyThrows
  private static void generateArchUnitFiles(
      Project project, Project appService, ModuleBuilder builder) {
    prepareParams(project, appService, builder);
    project
        .getLogger()
        .lifecycle("Injecting ArchitectureTest in module {}", appService.getProjectDir().getName());
    builder.setupFromTemplate("structure/applications/appservice/arch-validations");
    builder.appendDependencyToModule(
        Constants.APP_SERVICE,
        "testImplementation 'com.tngtech.archunit:archunit:" + Constants.ARCH_UNIT_VERSION + "'");
    builder.appendDependencyToModule(
        Constants.APP_SERVICE, "testImplementation 'com.fasterxml.jackson.core:jackson-databind'");
    builder.persist();
  }

  public static class DeclaredDependency {
    private String group;
    private String name;
    private List<DeclaredDependency> children;

    public DeclaredDependency(ResolvedDependency resolvedDependency) {
      this.group = resolvedDependency.getModuleGroup();
      this.name = resolvedDependency.getModuleName();
      this.children =
          resolvedDependency.getChildren().stream()
              .map(DeclaredDependency::new)
              .collect(Collectors.toList());
    }

    public Stream<DeclaredDependency> flattened() {
      return Stream.concat(
          Stream.of(this), children.stream().flatMap(DeclaredDependency::flattened));
    }
  }
}
