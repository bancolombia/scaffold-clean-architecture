package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.factory.upgrades.UpdateUtils.oneOfAll;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class ZUpdateGenericDependenciesVersion implements UpgradeAction {
  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return oneOfAll(
        UpdateUtils.updateVersions(
            builder, BUILD_GRADLE, "springBootVersion", Constants.SPRING_BOOT_VERSION),
        UpdateUtils.updateVersions(builder, BUILD_GRADLE, "sonarVersion", Constants.SONAR_VERSION),
        UpdateUtils.updateVersions(
            builder, BUILD_GRADLE, "jacocoVersion", Constants.JACOCO_VERSION),
        UpdateUtils.updateVersions(
            builder, BUILD_GRADLE, "coberturaVersion", Constants.COBERTURA_VERSION),
        UpdateUtils.updateVersions(
            builder, BUILD_GRADLE, "lombokVersion", Constants.LOMBOK_VERSION),
        UpdateUtils.updateVersions(
            builder, MAIN_GRADLE, "gradleVersion", Constants.GRADLE_WRAPPER_VERSION),
        UpdateUtils.updateVersions(
            builder, BUILD_GRADLE, "pitestVersion", Constants.GRADLE_PITEST_VERSION),
        UpdateUtils.updateVersions(
            builder, MAIN_GRADLE, "pitestVersion", Constants.PITEST_VERSION));
  }

  @Override
  public String name() {
    return "Spring|Sonar|Jacoco|Cobertura|Lombok|Gradle|Pitest update";
  }

  @Override
  public String description() {
    return "Update predefined dependencies to the latest plugin defined version";
  }
}
