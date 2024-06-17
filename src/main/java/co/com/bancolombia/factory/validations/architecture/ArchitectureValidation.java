package co.com.bancolombia.factory.validations.architecture;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.FileUtils;
import java.io.File;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.gradle.api.Project;

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

  @SneakyThrows
  private static void generateArchUnitFiles(
      Project project, Project appService, ModuleBuilder builder) {
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
}
