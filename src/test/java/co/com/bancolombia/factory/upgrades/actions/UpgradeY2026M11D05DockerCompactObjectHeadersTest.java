package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2026M11D05DockerCompactObjectHeadersTest {
  private static final String DOCKERFILE_TEMPLATE =
      """
      FROM eclipse-temurin:%s-jdk-alpine
      COPY *.jar app.jar
      ENV JAVA_OPTS="%s-XX:MaxRAMPercentage=70 -XX:+UseContainerSupport"
      ENTRYPOINT [ "/bin/sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
      """;

  @Mock private Project project;
  @Mock private Logger logger;

  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = new ModuleBuilder(project);
    updater = new UpgradeY2026M11D05DockerCompactObjectHeaders();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldAddFlagWhenUsingJava25() throws IOException {
    builder.addFile(DOCKERFILE, DOCKERFILE_TEMPLATE.formatted("25", ""));

    assertTrue(updater.up(builder));

    assertEquals(
        DOCKERFILE_TEMPLATE.formatted("25", "-XX:+UseCompactObjectHeaders "),
        builder.readFile(DOCKERFILE));
  }

  @Test
  void shouldAddFlagWhenJavaOptsStartsWithSpace() throws IOException {
    builder.addFile(DOCKERFILE, DOCKERFILE_TEMPLATE.formatted("25", " "));

    assertTrue(updater.up(builder));

    assertEquals(
        DOCKERFILE_TEMPLATE.formatted("25", "-XX:+UseCompactObjectHeaders "),
        builder.readFile(DOCKERFILE));
  }

  @Test
  void shouldNotAddFlagTwice() {
    builder.addFile(
        DOCKERFILE, DOCKERFILE_TEMPLATE.formatted("25", "-XX:+UseCompactObjectHeaders "));

    assertFalse(updater.up(builder));
  }

  @Test
  void shouldNotAddFlagWhenNotUsingJava25() throws IOException {
    String content = DOCKERFILE_TEMPLATE.formatted("21", "");
    builder.addFile(DOCKERFILE, content);

    assertFalse(updater.up(builder));
    assertEquals(content, builder.readFile(DOCKERFILE));
  }

  @Test
  void shouldSkipWhenThereIsNoDockerfile() {
    assertFalse(updater.up(builder));
  }
}
