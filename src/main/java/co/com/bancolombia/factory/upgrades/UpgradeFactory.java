package co.com.bancolombia.factory.upgrades;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class UpgradeFactory implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    getActions()
        .sorted(Comparator.comparing(clazz -> clazz.getClass().getSimpleName()))
        .filter(action -> action.up(builder))
        .forEach(
            action ->
                logger.lifecycle(
                    "Applying update with name {}: {}", action.name(), action.description()));
  }

  private Stream<UpgradeAction> getActions() {
    return new Reflections("co.com.bancolombia.factory.upgrades.actions", Scanners.SubTypes)
        .getSubTypesOf(UpgradeAction.class).stream().map(this::instantiate);
  }

  @NotNull
  @SneakyThrows
  private UpgradeAction instantiate(Class<? extends UpgradeAction> clazz) {
    return clazz.getDeclaredConstructor().newInstance();
  }
}
