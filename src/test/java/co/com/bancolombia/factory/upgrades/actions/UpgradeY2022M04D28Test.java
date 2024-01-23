package co.com.bancolombia.factory.upgrades.actions;

import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUpdater;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpgradeY2022M04D28Test {
  @Mock private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  public void setup() {
    updater = new UpgradeY2022M04D28();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    // Arrange
    // Act
    updater.up(builder);
    // Assert
    verify(builder, times(2)).updateFile(anyString(), any(FileUpdater.class));
  }
}
