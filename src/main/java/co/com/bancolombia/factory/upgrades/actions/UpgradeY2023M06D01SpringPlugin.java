package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M06D01SpringPlugin implements UpgradeAction {

  private static final String MATCH_ARG = "id.+org\\.springframework\\.boot.+\\n";
  private static final String APPEND_ARG =
      "id 'org.springframework.boot' version \"\\${springBootVersion}\" apply false\n";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateExpression(Constants.MainFiles.MAIN_GRADLE, MATCH_ARG, APPEND_ARG);
  }

  @Override
  public String name() {
    return "3.0.3->3.1.2";
  }

  @Override
  public String description() {
    return "Add apply false to spring boot plugin in root project";
  }
}
