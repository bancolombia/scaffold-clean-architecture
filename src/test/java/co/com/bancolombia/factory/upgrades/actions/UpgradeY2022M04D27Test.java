package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2022M04D27Test {
  @Mock private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  public void setup() {
    updater = new UpgradeY2022M04D27();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    // Arrange\
    builder.addFile(MAIN_GRADLE, "sourceCompatibility = JavaVersion.VERSION_1_8\n");
    builder.addFile(DOCKERFILE, "adoptopenjdk/openjdk8-openj9:alpine-slim\n");

    // Act
    updater.up(builder);
    // Assert
    verify(builder, atLeast(1))
        .updateExpression(MAIN_GRADLE, "JavaVersion.VERSION_1_8", "JavaVersion.VERSION_11");
    verify(builder, atLeast(1))
        .updateExpression(
            DOCKERFILE, "adoptopenjdk/openjdk8-openj9:alpine-slim", "eclipse-temurin:17-alpine");
  }
}
