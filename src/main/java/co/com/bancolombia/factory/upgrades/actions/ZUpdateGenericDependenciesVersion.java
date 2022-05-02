package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;

public class ZUpdateGenericDependenciesVersion implements UpgradeAction {
  @Override
  public boolean up(ModuleBuilder builder) {
    UpdateUtils.updateVersions(
        builder, BUILD_GRADLE, "springBootVersion", Constants.SPRING_BOOT_VERSION);
    UpdateUtils.updateVersions(builder, BUILD_GRADLE, "sonarVersion", Constants.SONAR_VERSION);
    UpdateUtils.updateVersions(builder, BUILD_GRADLE, "jacocoVersion", Constants.JACOCO_VERSION);
    UpdateUtils.updateVersions(
        builder, BUILD_GRADLE, "coberturaVersion", Constants.COBERTURA_VERSION);
    UpdateUtils.updateVersions(builder, BUILD_GRADLE, "lombokVersion", Constants.LOMBOK_VERSION);
    UpdateUtils.updateVersions(
        builder, MAIN_GRADLE, "gradleVersion", Constants.GRADLE_WRAPPER_VERSION);
    return true;
  }

  @Override
  public String name() {
    return "Spring|Sonar|Jacoco|Cobertura|Lombok|Gradle update";
  }

  @Override
  public String description() {
    return "Update predefined dependencies to the latest plugin defined version";
  }
}
