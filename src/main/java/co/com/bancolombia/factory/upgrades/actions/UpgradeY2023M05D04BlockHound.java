package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M05D04BlockHound implements UpgradeAction {
  private static final String CHECK = "org.junit.platform:junit-platform-launcher";
  private static final String MATCH = "compileOnly \"org.projectlombok:lombok";
  private static final String APPEND =
      "testImplementation 'io.projectreactor.tools:blockhound-junit-platform:"
          + Constants.BLOCK_HOUND_VERSION
          + "'\n"
          + "        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'\n\n        ";

  private static final String CHECK_ARG = "AllowRedefinitionToAddDeleteMethods";
  private static final String MATCH_ARG = "test.finalizedBy";
  private static final String APPEND_ARG =
      "tasks.withType(Test).configureEach {\n"
          + "        if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_13)) {\n"
          + "            jvmArgs += [\n"
          + "                    \"-XX:+AllowRedefinitionToAddDeleteMethods\"\n"
          + "            ]\n"
          + "        }\n"
          + "    }\n"
          + "\n"
          + "    ";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    if (builder.isReactive()) {
      return builder.updateFile(
          Constants.MainFiles.MAIN_GRADLE,
          content -> {
            String partial = UpdateUtils.insertBeforeMatch(content, MATCH, CHECK, APPEND);
            return UpdateUtils.insertBeforeMatch(partial, MATCH_ARG, CHECK_ARG, APPEND_ARG);
          });
    } else {
      return false;
    }
  }

  @Override
  public String name() {
    return "3.0.3->3.0.4";
  }

  @Override
  public String description() {
    return "Add block hound validations";
  }
}
