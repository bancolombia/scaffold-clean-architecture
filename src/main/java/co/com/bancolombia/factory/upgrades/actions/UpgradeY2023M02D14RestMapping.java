package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import org.gradle.api.Project;

public class UpgradeY2023M02D14RestMapping implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    String regex =
        "(Get|Post|Put|Delete|Patch|Request)Mapping\\((.*(path|value)\\s*=\\s*|)\\\".*(/)\\\".*\\)";
    Optional<Project> project =
        builder.getProject().getSubprojects().stream()
            .peek(p -> p.getLogger().lifecycle(p.getName()))
            .filter(p -> "api-rest".equals(p.getName()) || "reactive-web".equals(p.getName()))
            .findFirst();
    AtomicBoolean applied = new AtomicBoolean(false);
    project.ifPresent(
        subProject ->
            FileUtils.allFiles(
                subProject.getRootDir(),
                file -> apply(builder, regex, applied, file),
                (dir, name) -> name.endsWith(".java")));
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
      builder.getProject().getLogger().warn("Error updating file {}", file.getName(), e);
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
