package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpgradeY2022M04D27Test {
    @Mock
    private ModuleBuilder builder;
    private UpgradeAction updater;

    @Before
    public void setup() {
        updater = new UpgradeY2022M04D27();
        assertNotNull(updater.name());
        assertNotNull(updater.description());
    }

    @Test
    public void shouldApplyUpdate() throws IOException {
        // Arrange\
        builder.addFile(MAIN_GRADLE, "sourceCompatibility = JavaVersion.VERSION_1_8\n");
        builder.addFile(DOCKERFILE, "adoptopenjdk/openjdk8-openj9:alpine-slim\n");

        // Act
        updater.up(builder);
        // Assert
        verify(builder, atLeast(1))
                .updateExpression(MAIN_GRADLE, "JavaVersion.VERSION_1_8", "JavaVersion.VERSION_11");
        verify(builder, atLeast(1))
                .updateExpression(DOCKERFILE, "adoptopenjdk/openjdk8-openj9:alpine-slim", "adoptopenjdk/openjdk11-openj9:alpine-slim");
    }
}
