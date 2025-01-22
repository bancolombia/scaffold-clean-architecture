package co.com.bancolombia.utils.offline;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.UpdateSettings;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.OperationsProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder(setterPrefix = "with")
public class UpdateDependencies {
  public static final String DEPENDENCIES_CONFIG_FILE = "sh_dependencies.json";
  public static final String CONSTANTS_PATH = "src/main/java/co/com/bancolombia/Constants.java";

  @Builder.Default private ObjectMapper mapper = new ObjectMapper();
  @Builder.Default private ExternalOperations operations = OperationsProvider.real();
  @Builder.Default private String constantsPath = CONSTANTS_PATH;

  public static UpdateDependencies ofDefaults() {
    return UpdateDependencies.builder().build();
  }

  public void run() throws IOException {
    UpdateSettings settings =
        mapper.readValue(Paths.get(DEPENDENCIES_CONFIG_FILE).toFile(), UpdateSettings.class);
    File constantsFile = Paths.get(constantsPath).toFile();
    String content = FileUtils.readFileAsString(constantsFile, null);
    for (UpdateSettings.Dependency dependency : settings.getMaven()) {
      DependencyRelease current = DependencyRelease.from(dependency.getPackageName());
      current.setVersion(Constants.getVersion(dependency.getName()));
      DependencyRelease newest = operations.getTheLastDependencyRelease(current).orElse(current);
      if (newest.isNewest(current)) {
        content = updateVersion(content, dependency, newest.getVersion());
      }
    }
    for (UpdateSettings.Dependency dependency : settings.getGradle()) {
      DependencyRelease current = DependencyRelease.from(dependency.getPackageName());
      current.setVersion(Constants.getVersion(dependency.getName()));
      DependencyRelease newest = operations.getLatestGradlePluginVersion(current).orElse(current);
      if (newest.isNewest(current)) {
        content = updateVersion(content, dependency, newest.getVersion());
      }
    }
    for (UpdateSettings.Dependency dependency : settings.getCustom()) {
      Optional<String> dep = operations.getGradleWrapperFromFile();
      if (dep.isPresent()) {
        content = updateVersion(content, dependency, dep.get());
      }
    }
    FileUtils.writeContentToFile(content, constantsFile);
  }

  private String updateVersion(
      String content, UpdateSettings.Dependency dependency, String version) {
    return content.replaceFirst(
        dependency.getName() + " = .*;", dependency.getName() + " = \"" + version + "\";");
  }
}
