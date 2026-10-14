package co.com.bancolombia.plugins;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.Constants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.Action;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.provider.Providers;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.caching.configuration.BuildCacheConfiguration;
import org.gradle.caching.local.DirectoryBuildCache;
import org.gradle.plugin.management.PluginManagementSpec;
import org.gradle.plugin.management.PluginRequest;
import org.gradle.plugin.management.PluginResolutionStrategy;
import org.gradle.plugin.management.PluginResolveDetails;
import org.gradle.plugin.use.PluginId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SettingsConventionPluginTest {

  @Test
  @SuppressWarnings("unchecked")
  void shouldConfigureBuildCacheAndIncludeDiscoveredModules(@TempDir Path tempDir)
      throws IOException {
    // Arrange
    Files.createDirectories(tempDir.resolve("applications/app-service"));
    Files.createDirectories(tempDir.resolve("domain/model"));
    Files.createDirectories(tempDir.resolve("domain/usecase"));
    Files.createDirectories(tempDir.resolve("docs/ignored-module"));
    Files.createFile(tempDir.resolve("applications/app-service/build.gradle"));
    Files.createFile(tempDir.resolve("domain/model/build.gradle"));
    Files.createFile(tempDir.resolve("domain/usecase/build.gradle"));
    Files.createFile(tempDir.resolve("docs/ignored-module/build.gradle"));

    Settings settings = mock(Settings.class);
    when(settings.getRootDir()).thenReturn(tempDir.toFile());
    stubExcludeModuleProperty(settings, Providers.notDefined());

    PluginManagementSpec pluginManagement = mock(PluginManagementSpec.class);
    PluginResolutionStrategy pluginResolutionStrategy = mock(PluginResolutionStrategy.class);
    when(settings.getPluginManagement()).thenReturn(pluginManagement);
    when(pluginManagement.getResolutionStrategy()).thenReturn(pluginResolutionStrategy);
    doAnswer(invocation -> null).when(pluginResolutionStrategy).eachPlugin(any());

    BuildCacheConfiguration buildCacheConfiguration = mock(BuildCacheConfiguration.class);
    DirectoryBuildCache localBuildCache = mock(DirectoryBuildCache.class);
    doAnswer(
            invocation -> {
              Action<DirectoryBuildCache> action = invocation.getArgument(0);
              action.execute(localBuildCache);
              return null;
            })
        .when(buildCacheConfiguration)
        .local(any());
    when(settings.getBuildCache()).thenReturn(buildCacheConfiguration);

    Map<String, ProjectDescriptor> descriptors = new HashMap<>();
    when(settings.project(anyString()))
        .thenAnswer(
            invocation ->
                descriptors.computeIfAbsent(
                    invocation.getArgument(0), key -> mock(ProjectDescriptor.class)));

    SettingsConventionPlugin plugin = new SettingsConventionPlugin();

    // Act
    plugin.apply(settings);

    // Assert
    verify(settings).include(":app-service");
    verify(settings).include(":model");
    verify(settings).include(":usecase");
    verify(settings, never()).include(":ignored-module");
    assertTrue(descriptors.containsKey(":app-service"));
    assertTrue(descriptors.containsKey(":model"));
    assertTrue(descriptors.containsKey(":usecase"));
    verify(localBuildCache).setDirectory(tempDir.resolve("build-cache").toFile());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldSkipModulesListedInExcludeModuleProperty(@TempDir Path tempDir) throws IOException {
    // Arrange
    Files.createDirectories(tempDir.resolve("applications/app-service"));
    Files.createDirectories(tempDir.resolve("domain/model"));
    Files.createDirectories(tempDir.resolve("infrastructure/driven-adapters/rest-consumer"));
    Files.createFile(tempDir.resolve("applications/app-service/build.gradle"));
    Files.createFile(tempDir.resolve("domain/model/build.gradle"));
    Files.createFile(tempDir.resolve("infrastructure/driven-adapters/rest-consumer/build.gradle"));

    Settings settings = mock(Settings.class);
    when(settings.getRootDir()).thenReturn(tempDir.toFile());
    stubExcludeModuleProperty(settings, Providers.of("rest-consumer"));

    PluginManagementSpec pluginManagement = mock(PluginManagementSpec.class);
    PluginResolutionStrategy pluginResolutionStrategy = mock(PluginResolutionStrategy.class);
    when(settings.getPluginManagement()).thenReturn(pluginManagement);
    when(pluginManagement.getResolutionStrategy()).thenReturn(pluginResolutionStrategy);
    doAnswer(invocation -> null).when(pluginResolutionStrategy).eachPlugin(any());

    BuildCacheConfiguration buildCacheConfiguration = mock(BuildCacheConfiguration.class);
    DirectoryBuildCache localBuildCache = mock(DirectoryBuildCache.class);
    doAnswer(
            invocation -> {
              Action<DirectoryBuildCache> action = invocation.getArgument(0);
              action.execute(localBuildCache);
              return null;
            })
        .when(buildCacheConfiguration)
        .local(any());
    when(settings.getBuildCache()).thenReturn(buildCacheConfiguration);

    Map<String, ProjectDescriptor> descriptors = new HashMap<>();
    when(settings.project(anyString()))
        .thenAnswer(
            invocation ->
                descriptors.computeIfAbsent(
                    invocation.getArgument(0), key -> mock(ProjectDescriptor.class)));

    SettingsConventionPlugin plugin = new SettingsConventionPlugin();

    // Act
    plugin.apply(settings);

    // Assert
    verify(settings).include(":app-service");
    verify(settings).include(":model");
    verify(settings, never()).include(":rest-consumer");
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldHonorVersionsDeclaredByConsumerInPluginsBlock(@TempDir Path tempDir) {
    Settings settings = mock(Settings.class);
    when(settings.getRootDir()).thenReturn(tempDir.toFile());

    ProviderFactory providers = mock(ProviderFactory.class);
    when(settings.getProviders()).thenReturn(providers);
    when(providers.gradleProperty("excludeModule")).thenReturn(Providers.notDefined());

    PluginManagementSpec pluginManagement = mock(PluginManagementSpec.class);
    PluginResolutionStrategy pluginResolutionStrategy = mock(PluginResolutionStrategy.class);
    when(settings.getPluginManagement()).thenReturn(pluginManagement);
    when(pluginManagement.getResolutionStrategy()).thenReturn(pluginResolutionStrategy);
    AtomicReference<Action<PluginResolveDetails>> resolutionAction = new AtomicReference<>();
    doAnswer(
            invocation -> {
              resolutionAction.set(invocation.getArgument(0));
              return null;
            })
        .when(pluginResolutionStrategy)
        .eachPlugin(any());

    BuildCacheConfiguration buildCacheConfiguration = mock(BuildCacheConfiguration.class);
    DirectoryBuildCache localBuildCache = mock(DirectoryBuildCache.class);
    doAnswer(
            invocation -> {
              Action<DirectoryBuildCache> action = invocation.getArgument(0);
              action.execute(localBuildCache);
              return null;
            })
        .when(buildCacheConfiguration)
        .local(any());
    when(settings.getBuildCache()).thenReturn(buildCacheConfiguration);

    new SettingsConventionPlugin().apply(settings);

    assertResolvedPlugin(
        resolutionAction.get(),
        "org.springframework.boot",
        "4.2.0",
        "org.springframework.boot:spring-boot-gradle-plugin:4.2.0");
    assertResolvedPlugin(
        resolutionAction.get(),
        "info.solidsoft.pitest",
        "1.20.0",
        "info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.20.0");
    assertResolvedPlugin(
        resolutionAction.get(),
        "org.sonarqube",
        "7.6.0.1234",
        "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:7.6.0.1234");
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldUseConstantsVersionsWhenPluginRequestIsUnversioned(@TempDir Path tempDir) {
    Settings settings = mock(Settings.class);
    when(settings.getRootDir()).thenReturn(tempDir.toFile());

    ProviderFactory providers = mock(ProviderFactory.class);
    when(settings.getProviders()).thenReturn(providers);
    when(providers.gradleProperty("excludeModule")).thenReturn(Providers.notDefined());

    PluginManagementSpec pluginManagement = mock(PluginManagementSpec.class);
    PluginResolutionStrategy pluginResolutionStrategy = mock(PluginResolutionStrategy.class);
    when(settings.getPluginManagement()).thenReturn(pluginManagement);
    when(pluginManagement.getResolutionStrategy()).thenReturn(pluginResolutionStrategy);
    AtomicReference<Action<PluginResolveDetails>> resolutionAction = new AtomicReference<>();
    doAnswer(
            invocation -> {
              resolutionAction.set(invocation.getArgument(0));
              return null;
            })
        .when(pluginResolutionStrategy)
        .eachPlugin(any());

    BuildCacheConfiguration buildCacheConfiguration = mock(BuildCacheConfiguration.class);
    DirectoryBuildCache localBuildCache = mock(DirectoryBuildCache.class);
    doAnswer(
            invocation -> {
              Action<DirectoryBuildCache> action = invocation.getArgument(0);
              action.execute(localBuildCache);
              return null;
            })
        .when(buildCacheConfiguration)
        .local(any());
    when(settings.getBuildCache()).thenReturn(buildCacheConfiguration);

    new SettingsConventionPlugin().apply(settings);

    assertResolvedPlugin(
        resolutionAction.get(),
        "org.springframework.boot",
        null,
        "org.springframework.boot:spring-boot-gradle-plugin:" + Constants.SPRING_BOOT_VERSION);
    assertResolvedPlugin(
        resolutionAction.get(),
        "info.solidsoft.pitest",
        null,
        "info.solidsoft.gradle.pitest:gradle-pitest-plugin:" + Constants.GRADLE_PITEST_VERSION);
    assertResolvedPlugin(
        resolutionAction.get(),
        "org.sonarqube",
        null,
        "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:" + Constants.SONAR_VERSION);
  }

  private void assertResolvedPlugin(
      Action<PluginResolveDetails> resolutionAction,
      String pluginId,
      String requestedVersion,
      String expectedModule) {
    PluginResolveDetails details = mock(PluginResolveDetails.class);
    PluginRequest request = mock(PluginRequest.class);
    PluginId id = mock(PluginId.class);
    when(details.getRequested()).thenReturn(request);
    when(request.getId()).thenReturn(id);
    when(id.getId()).thenReturn(pluginId);
    when(request.getVersion()).thenReturn(requestedVersion);

    resolutionAction.execute(details);

    verify(details).useModule(expectedModule);
  }

  private void stubExcludeModuleProperty(Settings settings, Provider<String> value) {
    ProviderFactory providers = mock(ProviderFactory.class);
    when(settings.getProviders()).thenReturn(providers);
    when(providers.gradleProperty("excludeModule")).thenReturn(value);
  }
}
