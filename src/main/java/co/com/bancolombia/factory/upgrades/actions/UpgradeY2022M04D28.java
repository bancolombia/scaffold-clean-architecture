package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2022M04D28 implements UpgradeAction {
  public static final String DISABLE_PLAIN_JAR_CHECK = "jar {";
  public static final String DISABLE_PLAIN_JAR =
      "\n\n" + "jar {\n" + "    // To disable the *-plain.jar\n" + "    enabled = false\n" + "}";
  public static final String SET_JAR_NAME_CHECK = "bootJar {";
  public static final String SET_JAR_NAME =
      "\n\n"
          + "bootJar {\n"
          + "    // Sets output jar name\n"
          + "    archiveFileName = \"${project.getParent().getName()}.${archiveExtension.get()}\"\n"
          + "}";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return Boolean.logicalOr(
        UpdateUtils.appendIfNotContains(
            builder, APP_BUILD_GRADLE, DISABLE_PLAIN_JAR_CHECK, DISABLE_PLAIN_JAR),
        UpdateUtils.appendIfNotContains(
            builder, APP_BUILD_GRADLE, SET_JAR_NAME_CHECK, SET_JAR_NAME));
  }

  @Override
  public String name() {
    return "2.2.4->2.2.5";
  }

  @Override
  public String description() {
    return "Update gradle jar name, remove *-plain.jar generation";
  }
}
