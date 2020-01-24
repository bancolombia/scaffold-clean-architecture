package co.com.bancolombia.templates;

public class Constants {

    private Constants() {}

    public static final String JAVA_EXTENSION = ".java";

    /**
     * Base Dirs
     **/
    public static final String INFRASTRUCTURE = "infrastructure";
    public static final String DOMAIN = "domain";
    public static final String APPLICATION = "applications/app-service";

    /**
     * Child Dirs applications
     **/
    public static final String MAIN_JAVA = "src/main/java";
    public static final String MAIN_RESOURCES = "src/main/resources";
    public static final String TEST_JAVA = "src/test/java";
    public static final String CONFIG = "config";

    /**
     * Child Dirs Infrastructure
     **/

    public static final String DRIVEN_ADAPTERS = "driven-adapters";
    public static final String ENTRY_POINTS = "entry-points";
    public static final String HELPERS = "helpers";

    /**
     * Child Dirs Domain
     **/

    public static final String MODEL = "model";
    public static final String GATEWAYS = "gateways";
    public static final String REPOSITORY = "Repository";
    public static final String USECASE = "usecase";

    public static final String BUILD_GRADLE = "build.gradle";

    public static final String MAIN_GRADLE = "main.gradle";
    public static final String SETTINGS_GRADLE = "settings.gradle";
}