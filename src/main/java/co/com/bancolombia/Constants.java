package co.com.bancolombia;

import java.lang.reflect.Field;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  // All these constants are set automatically as params in ModuleBuilder
  public static final String PLUGIN_TASK_GROUP = "Clean Architecture";
  public static final String APP_SERVICE = "app-service";
  public static final String PATH_GRAPHQL = "/graphql";
  // dependencies
  public static final String SECRETS_VERSION = "4.4.29";
  public static final String SPRING_BOOT_VERSION = "3.4.2";
  public static final String LOMBOK_VERSION = "1.18.36";
  public static final String REACTIVE_COMMONS_VERSION = "5.2.3";
  public static final String REACTIVE_COMMONS_MAPPER_VERSION = "0.1.0";
  public static final String BLOCK_HOUND_VERSION = "1.0.10.RELEASE";
  public static final String AWS_BOM_VERSION = "2.30.11";
  public static final String COMMONS_JMS_VERSION = "2.3.9";
  public static final String GRAPHQL_KICKSTART_VERSION = "15.1.0";
  public static final String ARCH_UNIT_VERSION = "1.3.0";
  public static final String OKHTTP_VERSION = "4.12.0";
  public static final String RESILIENCE_4J_VERSION = "2.3.0";
  public static final String BIN_STASH_VERSION = "1.2.6";
  public static final String SPRING_DOC_OPENAPI_VERSION = "2.8.4";
  public static final String CLOUD_EVENTS_VERSION = "4.0.1";
  // gradle plugins
  public static final String JACOCO_VERSION = "0.8.12";
  public static final String SONAR_VERSION = "6.0.1.5171";
  public static final String COBERTURA_VERSION = "4.0.0";
  public static final String PLUGIN_VERSION = "3.20.13";
  public static final String DEPENDENCY_CHECK_VERSION = "12.0.2";
  public static final String PITEST_VERSION = "1.15.0";
  // custom
  public static final String GRADLE_WRAPPER_VERSION = "8.12.1";

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

  public static String getVersion(String name) {
    return Arrays.stream(Constants.class.getDeclaredFields())
        .filter(field -> field.getName().equalsIgnoreCase(name))
        .findFirst()
        .map(Constants::getValue)
        .orElse(null);
  }

  private static String getValue(Field field) {
    try {
      return field.get(null).toString();
    } catch (IllegalAccessException e) {
      return null;
    }
  }
}
