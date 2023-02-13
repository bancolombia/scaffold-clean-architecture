package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
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
      builder.appendDependencyToModule(
          "redis", "implementation 'jakarta.persistence:jakarta.persistence-api'");
      return apply(builder, repository) | apply(builder, repositoryAdapter);
    }
    return false;
  }

  private boolean apply(ModuleBuilder builder, String file) throws IOException {
    String prev = "org.springframework.data.repository.CrudRepository";
    String next = "org.springframework.data.keyvalue.repository.KeyValueRepository";
    return builder.updateFile(
        file,
        content -> {
          String res = UpdateUtils.replace(content, prev, next);
          res = UpdateUtils.replace(res, "CrudRepository", "KeyValueRepository");
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
