package co.com.bancolombia.utils.operations;

import static co.com.bancolombia.utils.operations.HttpOperations.*;
import static org.junit.jupiter.api.Assertions.*;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.http.HttpStatusException;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpOperationsTest {
  private static final String SHA256 =
      "21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607";

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
              case "/maven2/jakarta/servlet/jakarta.servlet-api/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(
                          "<metadata><groupId>jakarta.servlet</groupId><artifactId>jakarta.servlet-api</artifactId><versioning><latest>6.1.0</latest><release>6.1.0</release><versions><version>6.1.0</version></versions></versioning></metadata>");
              case "/maven2/unsafe/version/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(
                          "<metadata><groupId>unsafe</groupId><artifactId>version</artifactId><versioning><latest>1.0&apos; + evil + &apos;</latest><release>1.0&apos; + evil + &apos;</release><versions><version>1.0&apos; + evil + &apos;</version></versions></versioning></metadata>");
              case "/maven2/other/requested/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(
                          "<metadata><groupId>other</groupId><artifactId>different</artifactId><versioning><latest>9.9.9</latest><release>9.9.9</release><versions><version>9.9.9</version></versions></versioning></metadata>");
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
              case "/distributions/gradle-9.4.0-bin.zip.sha256" ->
                  new MockResponse().setResponseCode(200).setBody(SHA256 + "\n");
              case "/distributions/gradle-9.4.0-all.zip.sha256" ->
                  new MockResponse().setResponseCode(200).setBody("<html>not a checksum</html>");
              case "/maven2/server/error/maven-metadata.xml" ->
                  new MockResponse().setResponseCode(500);
              case "/incomplete/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody("<metadata><groupId>incomplete</groupId></metadata>");
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
                server.url("/versions/current").toString(),
                GRADLE_DISTRIBUTION_SHA256,
                server.url("/distributions/").toString() + "gradle-%version-%type.zip.sha256"));
  }

  private HttpOperations pluginPortalOperations() {
    return new HttpOperations(
        Map.of(GRADLE_PLUGINS, server.url("/") + "%group/%artifact/maven-metadata.xml"));
  }

  @Test
  void getVersionPlugin() {
    // Arrange: the installed plugin is 5.x.x (Constants.PLUGIN_VERSION)
    // Act
    Release result = found(pluginPortalOperations().findLatestPluginVersion());
    // Assert: highest stable 5.x (a .Final suffix is stable); neither 4.7.7 (the latest of the
    // portal), nor 6.0.0, nor the 5.2.0 RC/beta
    assertEquals("5.1.1.Final", result.getTagName());
  }

  @Test
  void shouldNotFindTheLatestPluginVersionWhenThePortalDoesNotHaveIt() {
    HttpOperations broken =
        new HttpOperations(Map.of(GRADLE_PLUGINS, server.url("/not-found").toString()));

    assertInstanceOf(LookupResult.NotFound.class, broken.findLatestPluginVersion());
  }

  @Test
  void shouldFailWhenThePluginMetadataIsIncomplete() {
    HttpOperations incomplete =
        new HttpOperations(
            Map.of(GRADLE_PLUGINS, server.url("/incomplete/maven-metadata.xml").toString()));

    assertInstanceOf(LookupResult.Failed.class, incomplete.findLatestPluginVersion());
  }

  @Test
  void shouldFailInsteadOfThrowingWhenAnEndpointIsMissing() {
    HttpOperations withoutEndpoints = new HttpOperations(Map.of());

    assertInstanceOf(LookupResult.Failed.class, withoutEndpoints.findLatestPluginVersion());
    assertInstanceOf(LookupResult.Failed.class, withoutEndpoints.findLatestGradleVersion());
    assertInstanceOf(
        LookupResult.Failed.class, withoutEndpoints.findGradleDistributionSha256("9.4.0", "bin"));
    assertInstanceOf(
        LookupResult.Failed.class,
        withoutEndpoints.findLatestDependency(DependencyRelease.from("group:artifact:1.0.0")));
  }

  @Test
  void getGradlePluginVersion() {
    // Arrange
    DependencyRelease release =
        DependencyRelease.from("org.springframework.boot:org.springframework.boot.gradle.plugin");
    // Act
    DependencyRelease result = found(operations.findLatestGradlePlugin(release));
    // Assert
    assertEquals("4.1.2", result.getVersion());
  }

  @Test
  void getGradlePluginVersionKeepsThePluginDeclaration() {
    // Arrange: as UpdateProjectDependencies reads it from id 'x' version 'y'
    DependencyRelease release = DependencyRelease.from("id org.springframework.boot version 4.0.0");
    String buildGradle = "id 'org.springframework.boot' version '4.0.0'";
    // Act
    DependencyRelease result = found(operations.findLatestGradlePlugin(release));
    // Assert
    assertTrue(result.isGradlePlugin());
    assertEquals(
        "id 'org.springframework.boot' version '4.1.2'",
        buildGradle.replaceAll(result.toRegex(), result.toString()));
  }

  @Test
  void getDependencyVersion() {
    // Arrange
    DependencyRelease release = DependencyRelease.from("some.dependency:name");
    // Act
    DependencyRelease result = found(operations.findLatestDependency(release));
    // Assert
    assertEquals("2.0.1", result.getVersion());
  }

  @Test
  void shouldFindTheLatestReleaseEvenWhenTheCurrentVersionIsNewer() {
    // Deciding whether to update is up to the caller
    DependencyRelease release = DependencyRelease.from("some.dependency:name:3.0.0");

    assertEquals("2.0.1", found(operations.findLatestDependency(release)).getVersion());
  }

  @Test
  void shouldNotFindADependencyThatTheRepositoryDoesNotHave() {
    LookupResult<DependencyRelease> result =
        mavenLikeOperations().findLatestDependency(DependencyRelease.from("internal:lib:1.0.0"));

    LookupResult.NotFound<?> notFound = assertInstanceOf(LookupResult.NotFound.class, result);
    assertEquals(
        server.url("/maven2/internal/lib/maven-metadata.xml").toString(), notFound.source());
  }

  @Test
  void shouldFailWhenTheRepositoryAnswersWithAnError() {
    LookupResult<DependencyRelease> result =
        mavenLikeOperations().findLatestDependency(DependencyRelease.from("server:error:1.0.0"));

    LookupResult.Failed<?> failed = assertInstanceOf(LookupResult.Failed.class, result);
    assertTrue(failed.reason().startsWith("HTTP 500"));
  }

  @Test
  void shouldFailWhenTheDependencyIsNotValid() {
    assertInstanceOf(
        LookupResult.Failed.class, operations.findLatestDependency(new DependencyRelease()));
    LookupResult.Failed<?> failed =
        assertInstanceOf(
            LookupResult.Failed.class, operations.findLatestGradlePlugin(new DependencyRelease()));
    // The failure names where the lookup goes, not the dependency again
    assertEquals(server.url("/maven-metadata.xml").toString(), failed.source());
  }

  @Test
  void getGradleWrapperVersion() {
    assertEquals(LookupResult.found("9.4.0"), operations.findLatestGradleVersion());
  }

  @Test
  void findGradleDistributionSha256() {
    assertEquals(
        LookupResult.found(SHA256), operations.findGradleDistributionSha256("9.4.0", "bin"));
  }

  @Test
  void findGradleDistributionSha256ShouldRejectInvalidContent() {
    assertInstanceOf(
        LookupResult.Failed.class, operations.findGradleDistributionSha256("9.4.0", "all"));
  }

  @Test
  void findGradleDistributionSha256ShouldNotBeFoundWhenTheVersionDoesNotExist() {
    assertInstanceOf(
        LookupResult.NotFound.class, operations.findGradleDistributionSha256("0.0.0", "bin"));
  }

  @Test
  void shouldKeepTheStatusCodeOfAnUnsuccessfulResponse() {
    String url = server.url("/missing").toString();

    HttpStatusException error =
        assertThrows(HttpStatusException.class, () -> RestConsumer.getTextRequest(url));

    assertEquals(404, error.getStatusCode());
    assertEquals(url, error.getUrl());
    assertTrue(error.isNotFound());
    assertEquals("HTTP 404 for " + url, error.getMessage());
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

  @Test
  void shouldGetMajorVersionOfAPluginVersion() {
    assertEquals(5, Utils.majorVersionOf("5.1.0").getAsInt());
    assertEquals(4, Utils.majorVersionOf("4.7.8").getAsInt());
  }

  @Test
  void shouldNotGetMajorVersionOfLocalPlaceholderVersions() {
    // sh_publish_plugin_local.sh publishes the plugin as CURRENT_LOCAL_VERSION
    assertTrue(Utils.majorVersionOf("CURRENT_LOCAL_VERSION").isEmpty());
    assertTrue(Utils.majorVersionOf("").isEmpty());
  }

  @Test
  void shouldKeepDotsInArtifactIdWhenBuildingMavenMetadataUrl() {
    DependencyRelease result =
        found(
            mavenLikeOperations()
                .findLatestDependency(
                    DependencyRelease.from("jakarta.servlet:jakarta.servlet-api:6.0.0")));

    assertEquals("6.1.0", result.getVersion());
  }

  @Test
  void shouldFailWithAReleaseWithUnsafeVersion() {
    assertInstanceOf(
        LookupResult.Failed.class,
        mavenLikeOperations().findLatestDependency(DependencyRelease.from("unsafe:version:0.1.0")));
  }

  @Test
  void shouldFailWithAReleaseOfADifferentArtifact() {
    assertInstanceOf(
        LookupResult.Failed.class,
        mavenLikeOperations()
            .findLatestDependency(DependencyRelease.from("other:requested:0.1.0")));
  }

  @SuppressWarnings("unchecked")
  private static <T> T found(LookupResult<T> result) {
    return ((LookupResult.Found<T>)
            assertInstanceOf(LookupResult.Found.class, result, () -> "Expected found: " + result))
        .value();
  }

  private HttpOperations mavenLikeOperations() {
    return new HttpOperations(
        Map.of(
            DEPENDENCY_RELEASES, server.url("/maven2/") + "%group/%artifact/maven-metadata.xml"));
  }
}
