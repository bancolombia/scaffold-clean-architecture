package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;

public class UpgradeY2023M02D08Redis implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    String repository =
        "infrastructure/driven-adapters/redis/src/main/java/"
            + builder.getParam("packagePath")
            + "/redis/repository/RedisRepository.java";
    String repositoryAdapter =
        "infrastructure/driven-adapters/redis/src/main/java/"
            + builder.getParam("packagePath")
            + "/redis/repository/helper/RepositoryAdapterOperations.java";

    if (builder.getProject().file(repository).getAbsoluteFile().exists()
        && builder.getProject().file(repositoryAdapter).getAbsoluteFile().exists()) {
      AtomicBoolean applied = new AtomicBoolean(false);
      apply(builder, repository, applied);
      apply(builder, repositoryAdapter, applied);
      builder.appendDependencyToModule(
          "redis", "implementation 'jakarta.persistence:jakarta.persistence-api'");
      return applied.get();
    }
    return false;
  }

  private void apply(ModuleBuilder builder, String file, AtomicBoolean applied) throws IOException {
    String prev = "org.springframework.data.repository.CrudRepository";
    String next = "org.springframework.data.keyvalue.repository.KeyValueRepository";
    builder.updateFile(
        file,
        content -> {
          String res = UpdateUtils.replace(content, prev, next);
          res = UpdateUtils.replace(res, "CrudRepository", "KeyValueRepository");
          if (!content.equals(res)) {
            applied.set(true);
          }
          return res;
        });
  }

  @Override
  public String name() {
    return "2.4.10->3.0.0";
  }

  @Override
  public String description() {
    return "Update Redis CrudRepository to KeyValueRepository";
  }
}
