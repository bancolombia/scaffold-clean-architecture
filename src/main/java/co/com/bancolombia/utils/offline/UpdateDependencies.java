package co.com.bancolombia.utils.offline;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.UpdateSettings;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.http.RestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class UpdateDependencies {

  public static final String DEPENDENCIES_CONFIG_FILE = "sh_dependencies.json";
  public static final String CONSTANTS_PATH = "src/main/java/co/com/bancolombia/Constants.java";

  public static void main(String[] args) throws IOException {
    RestService service = new RestService(true);
    ObjectMapper mapper = new ObjectMapper();
    UpdateSettings settings =
        mapper.readValue(Paths.get(DEPENDENCIES_CONFIG_FILE).toFile(), UpdateSettings.class);
    File constantsFile = Paths.get(CONSTANTS_PATH).toFile();
    String content = FileUtils.readFileAsString(constantsFile, null);
    for (UpdateSettings.Dependency dependency : settings.getMaven()) {
      Optional<DependencyRelease> dep =
          service.getTheLastDependencyRelease(DependencyRelease.from(dependency.getPackageName()));
      if (dep.isPresent()) {
        content = updateVersion(content, dependency, dep.get().getVersion());
      }
    }
    for (UpdateSettings.Dependency dependency : settings.getGradle()) {
      Optional<DependencyRelease> dep =
          service.getLatestGradlePluginVersion(DependencyRelease.from(dependency.getPackageName()));
      if (dep.isPresent()) {
        content = updateVersion(content, dependency, dep.get().getVersion());
      }
    }
    for (UpdateSettings.Dependency dependency : settings.getCustom()) {
      Optional<String> dep = service.getGradleWrapperFromFile();
      if (dep.isPresent()) {
        content = updateVersion(content, dependency, dep.get());
      }
    }
    FileUtils.writeContentToFile(content, constantsFile);
  }

  private static String updateVersion(
      String content, UpdateSettings.Dependency dependency, String version) {
    return content.replaceFirst(
        dependency.getName() + " = .*;", dependency.getName() + " = \"" + version + "\";");
  }
}
