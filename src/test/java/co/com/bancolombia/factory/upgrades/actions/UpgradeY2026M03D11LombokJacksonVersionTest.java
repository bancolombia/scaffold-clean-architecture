package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.LOMBOK_CONFIG;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2026M03D11LombokJacksonVersionTest {

  private static final String JACKSON_PROP = "lombok.jacksonized.jacksonVersion += 3";

  @Mock private Project project;
  @Mock private Logger logger;

  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());

    builder = spy(new ModuleBuilder(project));
    updater = new UpgradeY2026M03D11LombokJacksonVersion();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() {
    // Arrange
    String before = "config.stopBubbling = true\nlombok.addLombokGeneratedAnnotation = true";
    String expectedAfter =
        "config.stopBubbling = true\nlombok.addLombokGeneratedAnnotation = true\n" + JACKSON_PROP;
    builder.addFile(LOMBOK_CONFIG, before);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile(LOMBOK_CONFIG, expectedAfter);
  }

  @Test
  void shouldNotApplyWhenAlreadyContainsProperty() {
    // Arrange
    String expectedAfter =
        "config.stopBubbling = true\nlombok.addLombokGeneratedAnnotation = true\n" + JACKSON_PROP;
    builder.addFile(LOMBOK_CONFIG, expectedAfter);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertFalse(applied);
  }

  static Stream<Arguments> variantCases() {
    return Stream.of(
        Arguments.of(
            "value = false",
            "config.stopBubbling = true\nlombok.addLombokGeneratedAnnotation = false",
            "config.stopBubbling = true\nlombok.addLombokGeneratedAnnotation = false\n"
                + JACKSON_PROP),
        Arguments.of(
            "value = true with leading spaces",
            "config.stopBubbling = true\n  lombok.addLombokGeneratedAnnotation  =  true",
            "config.stopBubbling = true\n  lombok.addLombokGeneratedAnnotation  =  true\n"
                + JACKSON_PROP),
        Arguments.of(
            "value = custom",
            "config.stopBubbling =true\nlombok.addLombokGeneratedAnnotation=false",
            "config.stopBubbling =true\nlombok.addLombokGeneratedAnnotation=false\n"
                + JACKSON_PROP));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variantCases")
  void shouldApplyUpdateForVariants(String description, String before, String expectedAfter) {
    // Arrange
    builder.addFile(LOMBOK_CONFIG, before);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied, "Expected update to be applied for case: " + description);
    verify(builder).addFile(LOMBOK_CONFIG, expectedAfter);
  }
}
