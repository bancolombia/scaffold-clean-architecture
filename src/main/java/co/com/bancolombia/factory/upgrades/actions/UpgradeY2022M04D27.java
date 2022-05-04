package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import lombok.SneakyThrows;

public class UpgradeY2022M04D27 implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    builder.updateExpression(MAIN_GRADLE, "JavaVersion.VERSION_1_8", "JavaVersion.VERSION_11");
    try {
      builder.updateExpression(
          DOCKERFILE,
          "adoptopenjdk/openjdk8-openj9:alpine-slim",
          "adoptopenjdk/openjdk11-openj9:alpine-slim");
    } catch (IOException e) {
      builder.getProject().getLogger().debug("Could not update Dockerfile", e);
    }
    return true;
  }

  @Override
  public String name() {
    return "2.1.0->2.1.1";
  }

  @Override
  public String description() {
    return "upgrade the source compatibility and change the base image in dockerfile";
  }
}
