package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUpdater;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpgradeY2022M05D23Test {
  @Mock private ModuleBuilder builder;
  private UpgradeAction updater;

  @Before
  public void setup() {
    updater = new UpgradeY2022M05D23();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldApplyUpdate() throws IOException {
    // Arrange
    // Act
    updater.up(builder);
    // Assert
    verify(builder, times(1)).updateFile(anyString(), any(FileUpdater.class));
  }
}
