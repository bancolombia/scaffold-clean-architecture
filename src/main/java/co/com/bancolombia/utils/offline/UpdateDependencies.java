package co.com.bancolombia.utils.offline;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.UpdateSettings;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.DependencyLookup;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.LookupResult;
import co.com.bancolombia.utils.operations.OperationsProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import lombok.Setter;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import tools.jackson.databind.json.JsonMapper;

@Setter
@Builder(setterPrefix = "with")
public class UpdateDependencies {
  public static final String DEPENDENCIES_CONFIG_FILE = "sh_dependencies.json";
  public static final String CONSTANTS_PATH = "src/main/java/co/com/bancolombia/Constants.java";
  public static final String GRADLE_WRAPPER_SHA256 = "GRADLE_WRAPPER_SHA256";
  private static final Logger LOGGER = Logging.getLogger(UpdateDependencies.class);

  @Builder.Default private JsonMapper mapper = new JsonMapper();
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
    content = updateVersions(content, settings.getMaven(), operations::findLatestDependency);
    content = updateVersions(content, settings.getGradle(), operations::findLatestGradlePlugin);
    for (UpdateSettings.Dependency dependency : settings.getCustom()) {
      Optional<String> version =
          operations.findLatestGradleVersion().valueOrWarn(LOGGER, "the latest Gradle version");
      Optional<String> sha256 =
          version.flatMap(
              v ->
                  operations
                      .findGradleDistributionSha256(v, "bin")
                      .valueOrWarn(LOGGER, "the checksum of Gradle " + v + "-bin"));
      if (version.isPresent() && sha256.isPresent()) {
        content = updateVersion(content, dependency.getName(), version.get());
        content = updateVersion(content, GRADLE_WRAPPER_SHA256, sha256.get());
      }
    }
    FileUtils.writeContentToFile(content, constantsFile);
  }

  /** The settings name of each dependency is the Constants field that holds its version. */
  private String updateVersions(
      String content,
      List<UpdateSettings.Dependency> dependencies,
      Function<DependencyRelease, LookupResult<DependencyRelease>> finder) {
    // Looked up in the order of the settings; an artifact can hold the version of several fields
    List<DependencyRelease> current = new ArrayList<>();
    Map<DependencyRelease, List<String>> names = new LinkedHashMap<>();
    for (UpdateSettings.Dependency dependency : dependencies) {
      DependencyRelease release = DependencyRelease.from(dependency.getPackageName());
      release.setVersion(Constants.getVersion(dependency.getName()));
      current.add(release);
      names.computeIfAbsent(release, key -> new ArrayList<>()).add(dependency.getName());
    }
    String updated = content;
    int applied = 0;
    DependencyLookup.Result result = DependencyLookup.lookup(current, finder, LOGGER);
    for (DependencyLookup.Update update : result.updates()) {
      DependencyRelease latest = update.latest();
      for (String name : names.get(update.current())) {
        // Only the fields that are older: one field can already be newer than another
        if (UpdateUtils.isNewerVersion(Constants.getVersion(name), latest.getVersion())) {
          updated = updateVersion(updated, name, latest.getVersion());
          applied++;
          LOGGER.lifecycle("Dependency updated: {} ({})", latest, name);
        }
      }
    }
    result.logSummary(LOGGER, applied);
    return updated;
  }

  private String updateVersion(String content, String name, String version) {
    return content.replaceFirst(name + " =[^;]*;", name + " = \"" + version + "\";");
  }
}
