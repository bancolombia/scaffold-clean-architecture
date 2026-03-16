package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import lombok.SneakyThrows;

public class UpgradeY2023M02D14RestMapping implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    String regex =
        "(Get|Post|Put|Delete|Patch|Request)Mapping\\((.*(path|value)\\s*=\\s*|)\\\".*(/)\\\".*\\)";
    Optional<File> subProjectDir =
        Stream.of("api-rest", "reactive-web")
            .map(v -> builder.getChildProjectDirs().get(v))
            .filter(Objects::nonNull)
            .findFirst();
    AtomicBoolean applied = new AtomicBoolean(false);
    subProjectDir.ifPresent(
        dir ->
            FileUtils.allFiles(
                dir,
                file -> apply(builder, regex, applied, file),
                (d, name) -> name.endsWith(".java")));
    return applied.get();
  }

  private void apply(ModuleBuilder builder, String regex, AtomicBoolean applied, File file) {
    try {
      boolean isApplied =
          builder.updateFile(
              file.getAbsolutePath(), content -> Utils.replaceGroup(content, regex, "", 4));
      if (isApplied) {
        applied.set(true);
      }
    } catch (Exception e) {
      builder.getLogger().warn("Error updating file {}", file.getName(), e);
    }
  }

  @Override
  public String name() {
    return "2.4.10->3.0.0";
  }

  @Override
  public String description() {
    return "Update rest endpoint ending in /";
  }
}
