package co.com.bancolombia.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.utils.Utils;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DependencyReleaseTest {

  private static DependencyRelease release(String group, String artifact, String version) {
    DependencyRelease release = new DependencyRelease();
    release.setGroup(group);
    release.setArtifact(artifact);
    release.setVersion(version);
    return release;
  }

  private static String update(String content, DependencyRelease release) {
    return Utils.replaceExpression(content, release.toRegex(), release.toString());
  }

  @Test
  void shouldNotUpdateDependencyWhoseGroupOnlyMatchesWithWildcardDots() {
    String content = "implementation 'orgXflywaydb:flyway-core:9.0.0'";

    assertEquals(content, update(content, release("org.flywaydb", "flyway-core", "11.0.0")));
  }

  @Test
  void shouldUpdateOnlyTheMatchingDependencyWhenLineHasSeveralStrings() {
    String content =
        "implementation 'org.flywaydb:flyway-core:9.0.0', 'org.postgresql:postgresql:42.0.0'";

    assertEquals(
        "implementation 'org.flywaydb:flyway-core:11.0.0', 'org.postgresql:postgresql:42.0.0'",
        update(content, release("org.flywaydb", "flyway-core", "11.0.0")));
  }

  @Test
  void shouldUpdateDependencyWithDotsInArtifact() {
    String content = "implementation \"jakarta.servlet:jakarta.servlet-api:6.0.0\"";

    assertEquals(
        "implementation 'jakarta.servlet:jakarta.servlet-api:6.1.0'",
        update(content, release("jakarta.servlet", "jakarta.servlet-api", "6.1.0")));
  }

  @Test
  void shouldNotUpdateDependencyUsingVariableVersion() {
    String content = "implementation \"org.flywaydb:flyway-core:${flywayVersion}\"";

    assertEquals(content, update(content, release("org.flywaydb", "flyway-core", "11.0.0")));
  }

  @Test
  void shouldNotUpdatePluginWhoseIdOnlyMatchesWithWildcardDots() {
    DependencyRelease plugin = release("org.sonarqube", null, "7.0.0");
    plugin.setGradlePlugin(true);
    String content = "id 'orgXsonarqube' version '6.0.0'\nid 'org.sonarqube' version '6.0.0'";

    assertEquals(
        "id 'orgXsonarqube' version '6.0.0'\nid 'org.sonarqube' version '7.0.0'",
        update(content, plugin));
  }

  @Test
  void shouldFindEachDependencyWhenLineHasSeveralStrings() {
    String content =
        "implementation 'org.flywaydb:flyway-core:9.0.0', 'org.postgresql:postgresql:42.0.0'";

    assertEquals(
        Set.of("org.flywaydb:flyway-core:9.0.0", "org.postgresql:postgresql:42.0.0"),
        Utils.findExpressions(content, DependencyRelease.DECLARATION_REGEX));
  }

  @Test
  void shouldFindDependencyOnlyInsideItsOwnString() {
    String content =
        """
        implementation('org.flywaydb:flyway-core:9.0.0') { exclude group: 'x' }
        testImplementation 'org.mockito:mockito-core:5.0.0' // see 'docs'
        implementation "org.flywaydb:flyway-database-postgresql:${flywayVersion}"
        """;

    assertEquals(
        Set.of("org.flywaydb:flyway-core:9.0.0", "org.mockito:mockito-core:5.0.0"),
        Utils.findExpressions(content, DependencyRelease.DECLARATION_REGEX));
  }

  @Test
  void shouldNotFindTextThatIsNotADependency() {
    assertTrue(
        Utils.findExpressions("println 'Time: 10:30:00 now'", DependencyRelease.DECLARATION_REGEX)
            .isEmpty());
  }

  @Test
  void shouldNotFindNorUpdateAVersionWithSpaces() {
    String content = "implementation 'org.flywaydb:flyway-core:9.0.0 beta'";
    DependencyRelease flyway = release("org.flywaydb", "flyway-core", "11.0.0");

    assertTrue(Utils.findExpressions(content, DependencyRelease.DECLARATION_REGEX).isEmpty());
    assertEquals(content, update(content, flyway));
  }
}
