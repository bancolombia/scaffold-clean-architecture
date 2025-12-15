package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpgradeY2025M12D12SpringBoot4 implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getProject().getLogger();
    File root = builder.getProject().getRootDir();
    AtomicBoolean applied = new AtomicBoolean(false);
    FileUtils.allFiles(
        root, file -> apply(builder, file, applied, logger), (dir, name) -> name.endsWith(".java"));
    return applied.get();
  }

  private void apply(ModuleBuilder builder, File file, AtomicBoolean applied, Logger logger) {
    try {
      boolean appliedItem =
          builder.updateFile(
              file.getAbsolutePath(),
              content -> {
                String res =
                    UpdateUtils.replace(content, "org.springframework.boot.actuate.health.Health",
                            "org.springframework.boot.health.contributor.Health");
                res = UpdateUtils.replace(res, "org.springframework.boot.actuate.health.HealthIndicator",
                        "org.springframework.boot.health.contributor.HealthIndicator");
                res =
                    UpdateUtils.replace(
                        res, "org.springframework.boot.actuate.health.Status",
                            "org.springframework.boot.health.contributor.Status");
                res = UpdateUtils.replace(res,
                          "org.springframework.boot:spring-boot-actuator", "org.springframework.boot:spring-boot-health");
                res = UpdateUtils.replace(res,
                          "org.springframework.boot.autoconfigure.jms.JmsProperties",
                          "org.springframework.boot.jms.autoconfigure.JmsProperties");
                  res = UpdateUtils.replace(res,
                          "org.springframework.boot:spring-boot-starter-web",
                          "org.springframework.boot:spring-boot-starter-webmvc");
                  res = UpdateUtils.replace(res,
                          "org.springframework.boot.autoconfigure.jms.JmsProperties",
                          "org.springframework.boot.jms.autoconfigure.JmsProperties");
                  res =
                          UpdateUtils.replace(content, "com.fasterxml.jackson.databind",
                                  "tools.jackson.databind");
                  res = UpdateUtils.replace(res, "com.fasterxml.jackson.core",
                          "tools.jackson.core");
                  res =
                          UpdateUtils.replace(
                                  res, "com.fasterxml.jackson.core.JsonProcessingException",
                                  "tools.jackson.core.JacksonException");
                  res = UpdateUtils.replace(res,
                          "com.fasterxml.jackson.core", "tools.jackson.core");
                  res = UpdateUtils.replace(res,
                          "com.fasterxml.jackson.databind.JsonMappingException",
                          "tools.jackson.databind.JsonMappingException");

                return res;
              });
      if (appliedItem) {
        logger.debug("Spring Boot 3 to Spring Boot 4 applied in: {}", file.getName());
        applied.set(true);
      }
    } catch (IOException e) {
      logger.warn(
          "Error applying Spring Boot 4 migration on file {}", file.getAbsoluteFile(), e);

    }
  }

  @Override
  public String name() {
    return "3.28.0->4.0.0";
  }

  @Override
  public String description() {
    return "Update Spring Boot 3 imports to Spring Boot 4";
  }
}
