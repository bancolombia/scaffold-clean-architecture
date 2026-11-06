package co.com.bancolombia.utils.operations;

import static co.com.bancolombia.utils.operations.HttpOperations.*;
import static org.junit.jupiter.api.Assertions.*;

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
                          <groupId>org.springframework.boot</groupId>
                          <artifactId>org.springframework.boot.gradle.plugin</artifactId>
                          <version>4.2.0-RC1</version>
                          <versioning>
                            <latest>4.2.0-RC1</latest>
                            <release>4.2.0-RC1</release>
                            <versions>
                              <version>4.1.1</version>
                              <version>4.1.2</version>
                              <version>4.2.0-M1</version>
                              <version>4.2.0-M2</version>
                              <version>4.2.0-RC1</version>
                            </versions>
                            <lastUpdated>20261022170307</lastUpdated>
                          </versioning>
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
                          "<metadata><groupId>co.com.bancolombia.cleanArchitecture</groupId><artifactId>co.com.bancolombia.cleanArchitecture.gradle.plugin</artifactId><versioning><latest>4.7.7</latest><release>4.7.7</release><versions><version>v3.0.5</version><version>0.5</version><version>4.7.6</version><version>4.7.7</version><version>5.0.5</version><version>5.0.10</version><version>5.1.0</version><version>5.1.1.Final</version><version>5.2.0-RC1</version><version>5.2.0-beta</version><version>6.0.0</version></versions></versioning></metadata>");
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

  private HttpOperations pluginPortalOperations() {
    return new HttpOperations(
        Map.of(GRADLE_PLUGINS, server.url("/") + "%group/%artifact/maven-metadata.xml"));
  }

  @Test
  void getVersionPlugin() {
    // Arrange: the installed plugin is 5.x.x (Constants.PLUGIN_VERSION)
    // Act
    Release result = pluginPortalOperations().getLatestPluginVersion();
    // Assert: highest stable 5.x (a .Final suffix is stable); neither 4.7.7 (the latest of the
    // portal), nor 6.0.0, nor the 5.2.0 RC/beta
    assertEquals("5.1.1.Final", result.getTagName());
  }

  @Test
  void shouldReturnNullLatestPluginVersionWhenPluginPortalCanNotBeLoaded() {
    HttpOperations broken =
        new HttpOperations(Map.of(GRADLE_PLUGINS, server.url("/not-found").toString()));

    assertNull(broken.getLatestPluginVersion());
  }

  @Test
  void getGradlePluginVersion() {
    // Arrange
    DependencyRelease release =
        DependencyRelease.from("org.springframework.boot:org.springframework.boot.gradle.plugin");
    // Act
    Optional<DependencyRelease> result = operations.getLatestGradlePluginVersion(release);
    // Assert
    assertTrue(result.isPresent());
    assertEquals("4.1.2", result.get().getVersion());
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
}
