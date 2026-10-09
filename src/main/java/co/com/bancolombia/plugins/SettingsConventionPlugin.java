package co.com.bancolombia.plugins;

import co.com.bancolombia.Constants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class SettingsConventionPlugin implements Plugin<Settings> {
  private static final Logger LOGGER = Logging.getLogger(SettingsConventionPlugin.class);
  private static final String BUILD_GRADLE = "build.gradle";
  private static final String PITEST_PLUGIN_ID = "info.solidsoft.pitest";
  private static final String SONAR_PLUGIN_ID = "org.sonarqube";
  private static final String SPRING_BOOT_PLUGIN_ID = "org.springframework.boot";
  private static final String APPLICATIONS_DIR = "applications";
  private static final String DOMAIN_DIR = "domain";
  private static final String INFRASTRUCTURE_DIR = "infrastructure";
  private static final String EXCLUDE_MODULE_PROPERTY = "excludeModule";

  /** Applies settings conventions and dynamically includes Gradle modules. */
  @Override
  public void apply(Settings settings) {
    configureBuildCache(settings);
    configurePluginResolution(settings);
    includeModules(settings);
  }

  private Set<String> resolveExcludedModules(Settings settings) {
    String rawValue = settings.getProviders().gradleProperty(EXCLUDE_MODULE_PROPERTY).getOrElse("");
    return Arrays.stream(rawValue.split(","))
        .map(String::trim)
        .filter(module -> !module.isEmpty())
        .collect(Collectors.toSet());
  }

  private void configurePluginResolution(Settings settings) {
    settings
        .getPluginManagement()
        .getResolutionStrategy()
        .eachPlugin(
            pluginResolveDetails -> {
              String pluginId = pluginResolveDetails.getRequested().getId().getId();
              String pluginModule = pluginModule(pluginId);
              String requestedVersion = pluginResolveDetails.getRequested().getVersion();
              String version =
                  requestedVersion == null ? defaultPluginVersion(pluginId) : requestedVersion;
              if (pluginModule != null && version != null) {
                LOGGER.lifecycle("Resolved plugin {} with version: {}", pluginId, version);
                pluginResolveDetails.useModule(pluginModule + ":" + version);
              }
            });
  }

  private String pluginModule(String pluginId) {
    if (SPRING_BOOT_PLUGIN_ID.equals(pluginId)) {
      return "org.springframework.boot:spring-boot-gradle-plugin";
    }
    if (PITEST_PLUGIN_ID.equals(pluginId)) {
      return "info.solidsoft.gradle.pitest:gradle-pitest-plugin";
    }
    if (SONAR_PLUGIN_ID.equals(pluginId)) {
      return "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin";
    }
    return null;
  }

  private String defaultPluginVersion(String pluginId) {
    if (SPRING_BOOT_PLUGIN_ID.equals(pluginId)) {
      return Constants.SPRING_BOOT_VERSION;
    }
    if (PITEST_PLUGIN_ID.equals(pluginId)) {
      return Constants.GRADLE_PITEST_VERSION;
    }
    if (SONAR_PLUGIN_ID.equals(pluginId)) {
      return Constants.SONAR_VERSION;
    }
    return null;
  }

  private void configureBuildCache(Settings settings) {
    settings
        .getBuildCache()
        .local(
            localBuildCache ->
                localBuildCache.setDirectory(
                    new java.io.File(settings.getRootDir(), "build-cache")));
  }

  private void includeModules(Settings settings) {
    Path rootPath = settings.getRootDir().toPath();
    Set<String> excludedModules = resolveExcludedModules(settings);

    try (Stream<Path> pathStream = Files.walk(rootPath)) {
      List<Path> modulePaths =
          pathStream
              .filter(path -> path.getFileName().toString().equals(BUILD_GRADLE))
              .map(Path::getParent)
              .filter(path -> !path.equals(rootPath))
              .filter(path -> isIncludedDirectory(rootPath, path))
              .filter(path -> !excludedModules.contains(path.getFileName().toString()))
              .sorted(Comparator.naturalOrder())
              .toList();

      modulePaths.forEach(modulePath -> includeModule(settings, modulePath));
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to discover generated modules", exception);
    }
  }

  private boolean isIncludedDirectory(Path rootPath, Path modulePath) {
    Path relativePath = rootPath.relativize(modulePath);
    if (relativePath.getNameCount() == 0) {
      return false;
    }
    String topLevelDirectory = relativePath.getName(0).toString();
    return APPLICATIONS_DIR.equals(topLevelDirectory)
        || DOMAIN_DIR.equals(topLevelDirectory)
        || INFRASTRUCTURE_DIR.equals(topLevelDirectory);
  }

  private void includeModule(Settings settings, Path modulePath) {
    String projectPath = ":" + modulePath.getFileName();

    settings.include(projectPath);
    settings.project(projectPath).setProjectDir(modulePath.toFile());
  }
}
