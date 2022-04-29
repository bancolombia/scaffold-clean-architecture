package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2022M04D28 implements UpgradeAction {
  private boolean applied;
  public static final String DISABLE_PLAIN_JAR_CHECK = "jar {";
  private static final String DISABLE_PLAIN_JAR =
      "\n" + "jar {\n" + "    // To disable the *-plain.jar\n" + "    enabled = false\n" + "}";
  public static final String SET_JAR_NAME_CHECK = "bootJar {";
  public static final String SET_JAR_NAME =
      "\n"
          + "bootJar {\n"
          + "    // Sets output jar name\n"
          + "    archiveFileName = \"${project.getParent().getName()}.${archiveExtension.get()}\"\n"
          + "}";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    builder.updateFile(
        APP_BUILD_GRADLE,
        content -> {
          if (!content.contains(DISABLE_PLAIN_JAR_CHECK)) {
            applied = true;
            return content + DISABLE_PLAIN_JAR;
          }
          return content;
        });
    builder.updateFile(
        APP_BUILD_GRADLE,
        content -> {
          if (!content.contains(SET_JAR_NAME_CHECK)) {
            applied = true;
            return content + SET_JAR_NAME;
          }
          return content;
        });
    return applied;
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
