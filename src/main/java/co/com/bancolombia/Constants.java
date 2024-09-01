package co.com.bancolombia;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  // All these constants are set automatically as params in ModuleBuilder
  public static final String PLUGIN_TASK_GROUP = "Clean Architecture";
  public static final String APP_SERVICE = "app-service";
  public static final String PATH_GRAPHQL = "/graphql";
  // dependencies
  public static final String SECRETS_VERSION = "4.4.19";
  public static final String SPRING_BOOT_VERSION = "3.3.3";
  public static final String LOMBOK_VERSION = "1.18.34";
  public static final String REACTIVE_COMMONS_VERSION = "4.1.4";
  public static final String REACTIVE_COMMONS_MAPPER_VERSION = "0.1.0";
  public static final String BLOCK_HOUND_VERSION = "1.0.9.RELEASE";
  public static final String AWS_BOM_VERSION = "2.27.17";
  public static final String COMMONS_JMS_VERSION = "2.2.17";
  public static final String GRAPHQL_KICKSTART_VERSION = "15.1.0";
  public static final String ARCH_UNIT_VERSION = "1.3.0";
  public static final String OKHTTP_VERSION = "4.12.0";
  public static final String RESILIENCE_4J_VERSION = "2.2.0";
  public static final String BIN_STASH_VERSION = "1.2.6";
  public static final String SPRING_DOC_OPENAPI_VERSION = "2.6.0";
  // gradle plugins
  public static final String JACOCO_VERSION = "0.8.12";
  public static final String SONAR_VERSION = "5.1.0.4882";
  public static final String COBERTURA_VERSION = "4.0.0";
  public static final String PLUGIN_VERSION = "3.17.19";
  public static final String DEPENDENCY_CHECK_VERSION = "10.0.4";
  public static final String PITEST_VERSION = "1.15.0";
  // custom
  public static final String GRADLE_WRAPPER_VERSION = "8.10";

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class MainFiles {
    public static final String BUILD_GRADLE = "./build.gradle";
    public static final String MAIN_GRADLE = "./main.gradle";
    public static final String SETTINGS_GRADLE = "./settings.gradle";
    public static final String DOCKERFILE = "./deployment/Dockerfile";
    public static final String APP_BUILD_GRADLE = "./applications/app-service/build.gradle";
    public static final String APPLICATION_PROPERTIES =
        "applications/app-service/src/main/resources/application.yaml";
    public static final String GRADLE_PROPERTIES = "./gradle.properties";
  }
}
