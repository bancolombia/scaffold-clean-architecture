package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gradle.api.logging.Logger;

public class UpgradeY2025M12D12SpringBoot4 implements UpgradeAction {

  @Override
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getProject().getLogger();
    File root = builder.getProject().getRootDir();
    AtomicBoolean applied = new AtomicBoolean(false);
    FileUtils.allFiles(
        root,
        file -> apply(builder, file, applied, logger),
        (dir, name) -> name.endsWith(".java") || name.endsWith(".gradle"));
    return applied.get();
  }

  private void apply(ModuleBuilder builder, File file, AtomicBoolean applied, Logger logger) {
    try {
      boolean appliedItem =
          builder.updateFile(
              file.getAbsolutePath(),
              content -> {
                // Spring Boot Actuator package changes
                String updatedContent =
                    UpdateUtils.replace(
                        content,
                        "org.springframework.boot.actuate.health",
                        "org.springframework.boot.health.contributor");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "\"org.springframework.boot:spring-boot-actuator\"",
                        "\"org.springframework.boot:spring-boot-health\"");

                // Web starters renamed
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "\"org.springframework.boot:spring-boot-starter-web\"",
                        "\"org.springframework.boot:spring-boot-starter-webmvc\"");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "'org.springframework.boot:spring-boot-starter-web'",
                        "'org.springframework.boot:spring-boot-starter-webmvc'");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "\"org.springframework.boot:spring-boot-starter-web-services\"",
                        "\"org.springframework.boot:spring-boot-starter-webservices\"");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "'org.springframework.boot:spring-boot-starter-web-services'",
                        "'org.springframework.boot:spring-boot-starter-webservices'");

                // Reactive Web changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.test.autoconfigure.web.reactive",
                        "org.springframework.boot.webflux.test.autoconfigure");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.web.reactive",
                        "org.springframework.boot.webflux.autoconfigure");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.web.reactive",
                        "org.springframework.boot.webflux");

                // JMS package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.jms",
                        "org.springframework.boot.jms.autoconfigure");

                // MongoDB package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.mongo",
                        "org.springframework.boot.mongodb.autoconfigure");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.data.mongo",
                        "org.springframework.boot.mongodb.autoconfigure.data");

                // Redis package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.data.redis",
                        "org.springframework.boot.redis.autoconfigure");

                // Security package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "\"org.springframework.boot:spring-boot-starter-oauth2-resource-server\"",
                        "\"org.springframework.boot:spring-boot-starter-security-oauth2-resource-server\"");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "'org.springframework.boot:spring-boot-starter-oauth2-resource-server'",
                        "'org.springframework.boot:spring-boot-starter-security-oauth2-resource-server'");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "\"org.springframework.boot:spring-boot-starter-oauth2-client\"",
                        "\"org.springframework.boot:spring-boot-starter-security-oauth2-client\"");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "'org.springframework.boot:spring-boot-starter-oauth2-client'",
                        "'org.springframework.boot:spring-boot-starter-security-oauth2-client'");

                // Kafka package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.kafka",
                        "org.springframework.boot.kafka.autoconfigure");

                // AMQP/RabbitMQ package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.amqp",
                        "org.springframework.boot.amqp.autoconfigure");

                // Jackson package changes (com.fasterxml moved to tools)
                updatedContent = UpdateUtils.replace(updatedContent, "com.fasterxml", "tools");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent, "JsonProcessingException", "JacksonException");

                // Validation package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.validation",
                        "org.springframework.boot.validation.autoconfigure");

                // Cache package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.cache",
                        "org.springframework.boot.cache.autoconfigure");

                // JDBC package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.jdbc",
                        "org.springframework.boot.jdbc.autoconfigure");

                // Elasticsearch package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.elasticsearch",
                        "org.springframework.boot.elasticsearch.autoconfigure");

                return updatedContent;
              });
      if (appliedItem) {
        logger.debug("Spring Boot 3 to Spring Boot 4 applied in: {}", file.getName());
        applied.set(true);
      }
    } catch (IOException e) {
      logger.warn("Error applying Spring Boot 4 migration on file {}", file.getAbsoluteFile(), e);
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
