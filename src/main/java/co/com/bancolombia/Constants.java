package co.com.bancolombia;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String PLUGIN_TASK_GROUP = "Clean Architecture";
    public static final String SPRING_BOOT_VERSION = "2.1.1.RELEASE";
    public static final String SPRING_CLOUD_VERSION = "Greenwich.M1";
    public static final String SONAR_VERSION = "2.7";
    public static final String JACOCO_VERSION = "0.8.5";
    public static final String PLUGIN_VERSION = "1.6.1";
    public static final String SECRETS_VERSION = "2.1.0";
    public static final String RCOMMONS_ASYNC_COMMONS_STARTER_VERSION = "0.4.7";
    public static final String RCOMMONS_OBJECT_MAPPER_VERSION = "0.1.0";

    public enum BooleanOption {
        TRUE, FALSE
    }
}
