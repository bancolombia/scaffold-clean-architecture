package co.com.bancolombia;

public class Constants {
    /** Base Dirs **/
    public static final String infraestucture = "infraestucture";
    public static final String domain = "domain";
    public static final String application = "applications/app-service";

    /** Child Dirs applications **/
    public static final String mainJava = "src/main/java";
    public static final String mainResource = "src/main/resources";
    public static final String config = "config";
    public static final String app = "app";

    /** Child Dirs Infraestructure **/

    public static final String drivenAdapters = "driven-adapters";
    public static final String entryPoints = "entry-points";
    public static final String helpers = "helpers";

    /** Child Dirs Domain **/

    public static final String model = "model";
    public static final String usecase = "usecase";

    public static final String buildGradle = "build.gradle";
    public static final String applicationProperties = "application.yaml";
    public static final String log4j = "log4j2.properties";
    public static final String mainApplication = "MainApplication.java";
    public static final String mainGradle = "main.gradle";
    public static final String lombokConfig = "lombok.config";


    public static final String buildGradleModel = "dependencies {\n" +
            "    testCompile group: 'junit', name: 'junit', version: '4.12'\n" +
            "}\n";

    public static final String buildGradleUseCase = "dependencies {\n" +
            "    compile project(':domain-model')\n" +
            "    testCompile group: 'junit', name: 'junit', version: '4.12'\n" +
            "}";
    public static final String settingsGradle = "settings.gradle";

    public static final String testJava = "src/test/java";
    public static final String testResource = "src/test/resources";
}
