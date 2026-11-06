package co.com.bancolombia.utils.operations;

import static co.com.bancolombia.utils.operations.HttpOperations.DEPENDENCY_RELEASES;
import static co.com.bancolombia.utils.operations.HttpOperations.GRADLE_PLUGINS;
import static co.com.bancolombia.utils.operations.HttpOperations.GRADLE_VERSIONS_API;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpOperationsTest {

  private final MockWebServer server = new MockWebServer();
  private HttpOperations operations;

  @BeforeEach
  void setup() throws IOException {
    String gradleVersionResponse = "{\"version\":\"9.4.0\"}";
    String dependencyResponse =
        """
                <metadata>
                  <groupId>some.dependency</groupId>
                  <artifactId>name</artifactId>
                  <versioning>
                    <latest>2.0.1</latest>
                    <release>2.0.1</release>
                    <versions>
                      <version>0.0.1</version>
                    </versions>
                  </versioning>
                </metadata>
                """;
    String xmlResponse =
        """
                <metadata>
                  <groupId>org.sonarqube</groupId>
                  <artifactId>org.sonarqube.gradle.plugin</artifactId>
                  <version>4.4.1.3373</version>
                </metadata>
                """;
    final Dispatcher dispatcher =
        new Dispatcher() {
          @Override
          public @NotNull MockResponse dispatch(RecordedRequest request) {
            return switch (Objects.requireNonNull(request.getPath())) {
              case "/name/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(dependencyResponse);
              case "/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(xmlResponse);
              case "/co/com/bancolombia/cleanArchitecture/co.com.bancolombia.cleanArchitecture.gradle.plugin/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(
                          "<metadata><groupId>co.com.bancolombia.cleanArchitecture</groupId><artifactId>co.com.bancolombia.cleanArchitecture.gradle.plugin</artifactId><versioning><latest>4.7.7</latest><release>4.7.7</release><versions><version>v3.0.5</version><version>0.5</version><version>4.7.6</version><version>4.7.7</version><version>5.0.5</version><version>5.0.10</version><version>5.1.0</version><version>5.2.0-RC1</version><version>5.2.0-beta</version><version>6.0.0</version><version>7.0.0.Final</version><version>7.1.0-RC1</version></versions></versioning></metadata>");
              case "/versions/current" ->
                  new MockResponse().setResponseCode(200).setBody(gradleVersionResponse);
              case "/analytics" -> new MockResponse().setResponseCode(201);
              default -> new MockResponse().setResponseCode(404);
            };
          }
        };
    server.setDispatcher(dispatcher);
    server.start();
    operations =
        new HttpOperations(
            Map.of(
                DEPENDENCY_RELEASES,
                server.url("name/maven-metadata.xml").toString(),
                GRADLE_PLUGINS,
                server.url("/maven-metadata.xml").toString(),
                GRADLE_VERSIONS_API,
                server.url("/versions/current").toString()));
  }

  @Test
  void getVersionPlugin() {
    // Arrange: the installed plugin is 4.x.x (Constants.PLUGIN_VERSION)
    // Act
    Release result = pluginPortalOperations().getLatestPluginVersion();
    // Assert: latest stable of the same major version, not the newest of another one (5.1.0, 6.0.0)
    assertEquals("4.7.7", result.getTagName());
  }

  @Test
  void getGradlePluginVersion() {
    // Arrange
    DependencyRelease release = DependencyRelease.from("org.sonarqube:org.sonarqube.gradle.plugin");
    // Act
    Optional<DependencyRelease> result = operations.getLatestGradlePluginVersion(release);
    // Assert
    assertTrue(result.isPresent());
    assertEquals("4.4.1.3373", result.get().getVersion());
  }

  @Test
  void getDependencyVersion() {
    // Arrange
    DependencyRelease release = DependencyRelease.from("some.dependency:name");
    // Act
    Optional<DependencyRelease> result = operations.getTheLastDependencyRelease(release);
    // Assert
    assertTrue(result.isPresent());
    assertEquals("2.0.1", result.get().getVersion());
  }

  @Test
  void getGradleWrapperVersion() {
    // Arrange
    // Act
    Optional<String> version = operations.getGradleWrapperFromFile();
    // Assert
    assertTrue(version.isPresent());
    assertEquals("9.4.0", version.get());
  }

  @Test
  void sendAnalytics() throws Exception {
    // Arrange
    HttpUrl baseUrl = server.url("/analytics");
    String body = "somebody";
    // Act
    String result = RestConsumer.postRequest(baseUrl.toString(), body, String.class);
    // Assert
    assertNull(result);
  }

  private HttpOperations pluginPortalOperations() {
    return new HttpOperations(
        Map.of(GRADLE_PLUGINS, server.url("/") + "%group/%artifact/maven-metadata.xml"));
  }

  @Test
  void shouldGetLatestStableReleaseOfMajorEvenWhenAnotherMajorIsNewer() {
    Optional<Release> result = pluginPortalOperations().getLatestPluginVersion(5);

    assertTrue(result.isPresent());
    // 4.7.7 is the portal's latest and 6.0.0 exists, but only 5.x.x versions are considered
    assertEquals("5.1.0", result.get().getTagName());
  }

  @Test
  void shouldCompareVersionsNumericallyAndIgnoreUnstableOrUnknownVersions() {
    // 5.0.10 > 5.0.5 numerically; 5.2.0-RC1, 5.2.0-beta, v3.0.5 and 0.5 are not stable x.y.z
    assertEquals(
        "5.1.0", pluginPortalOperations().getLatestPluginVersion(5).orElseThrow().getTagName());
    assertEquals(
        "4.7.7", pluginPortalOperations().getLatestPluginVersion(4).orElseThrow().getTagName());
    assertEquals(
        "6.0.0", pluginPortalOperations().getLatestPluginVersion(6).orElseThrow().getTagName());
  }

  @Test
  void shouldAcceptFinalSuffixAsStableAndRejectReleaseCandidates() {
    assertEquals(
        "7.0.0.Final",
        pluginPortalOperations().getLatestPluginVersion(7).orElseThrow().getTagName());
  }

  @Test
  void shouldReturnEmptyWhenThereIsNoStableReleaseOfMajor() {
    assertTrue(pluginPortalOperations().getLatestPluginVersion(8).isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenPluginPortalCanNotBeLoaded() {
    HttpOperations broken =
        new HttpOperations(Map.of(GRADLE_PLUGINS, server.url("/not-found").toString()));

    assertTrue(broken.getLatestPluginVersion(5).isEmpty());
  }

  @Test
  void shouldReturnNullLatestPluginVersionWhenPluginPortalCanNotBeLoaded() {
    HttpOperations broken =
        new HttpOperations(Map.of(GRADLE_PLUGINS, server.url("/not-found").toString()));

    assertNull(broken.getLatestPluginVersion());
  }
}
