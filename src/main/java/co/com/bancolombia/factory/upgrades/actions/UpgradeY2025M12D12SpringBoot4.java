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
                        "org.springframework.boot.actuate.health.Health",
                        "org.springframework.boot.health.contributor.Health");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.actuate.health.HealthIndicator",
                        "org.springframework.boot.health.contributor.HealthIndicator");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.actuate.health.Status",
                        "org.springframework.boot.health.contributor.Status");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.actuate.health.HealthComponent",
                        "org.springframework.boot.health.contributor.HealthComponent");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.actuate.health.ReactiveHealthIndicator",
                        "org.springframework.boot.health.contributor.ReactiveHealthIndicator");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.actuate.health.HealthContributor",
                        "org.springframework.boot.health.contributor.HealthContributor");
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

                // JMS package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.jms.JmsProperties",
                        "org.springframework.boot.jms.autoconfigure.JmsProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration",
                        "org.springframework.boot.jms.autoconfigure.JmsAutoConfiguration");

                // MongoDB package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer",
                        "org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory",
                        "org.springframework.boot.mongodb.autoconfigure.ReactiveMongoClientFactory");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.mongo.MongoProperties",
                        "org.springframework.boot.mongodb.autoconfigure.MongoProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
                        "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.data.mongo",
                        "org.springframework.boot.mongodb.autoconfigure.data");

                // Redis package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.data.redis.RedisProperties",
                        "org.springframework.boot.redis.autoconfigure.RedisProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
                        "org.springframework.boot.redis.autoconfigure.RedisAutoConfiguration");

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
                        "org.springframework.boot.autoconfigure.kafka.KafkaProperties",
                        "org.springframework.boot.kafka.autoconfigure.KafkaProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
                        "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration");

                // AMQP/RabbitMQ package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.amqp.RabbitProperties",
                        "org.springframework.boot.amqp.autoconfigure.RabbitProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
                        "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration");

                // Jackson package changes (com.fasterxml moved to tools)
                updatedContent = UpdateUtils.replace(updatedContent, "com.fasterxml", "tools");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent, "JsonProcessingException", "JacksonException");

                // Validation package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration",
                        "org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration");

                // Cache package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.cache.CacheProperties",
                        "org.springframework.boot.cache.autoconfigure.CacheProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration",
                        "org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration");

                // JDBC package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.jdbc.DataSourceProperties",
                        "org.springframework.boot.jdbc.autoconfigure.DataSourceProperties");
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration");

                // Elasticsearch package changes
                updatedContent =
                    UpdateUtils.replace(
                        updatedContent,
                        "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties",
                        "org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchProperties");

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
