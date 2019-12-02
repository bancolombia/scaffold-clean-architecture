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
    public static final String mainApplication = "MainApplication.java";

    public static final String log4j = "log4j2.properties";
    public static final String mainGradle = "main.gradle";
    public static final String lombokConfig = "lombok.config";
    public static final String settingsGradle = "settings.gradle";
    public static final String gitignore = ".gitignore";
    public static final String readMe = "Readme.md";




    public static final String buildGradleUseCaseContent = "dependencies {\n" +
            "    compile project(':domain-model')\n" +
            "}";

    public static final String lombokConfigContent = "lombok.addLombokGeneratedAnnotation = true";
    public static final String gitIgnoreContent = "##############################\n" +
            "## Java\n" +
            "##############################\n" +
            ".mtj.tmp/\n" +
            "*.class\n" +
            "*.jar\n" +
            "*.war\n" +
            "*.ear\n" +
            "*.nar\n" +
            "hs_err_pid*\n" +
            "\n" +
            "##############################\n" +
            "## Maven\n" +
            "##############################\n" +
            "target/\n" +
            "pom.xml.tag\n" +
            "pom.xml.releaseBackup\n" +
            "pom.xml.versionsBackup\n" +
            "pom.xml.next\n" +
            "release.properties\n" +
            "dependency-reduced-pom.xml\n" +
            "buildNumber.properties\n" +
            ".mvn/timing.properties\n" +
            ".mvn/wrapper/maven-wrapper.jar\n" +
            "\n" +
            "##############################\n" +
            "## Gradle\n" +
            "##############################\n" +
            "bin/\n" +
            "build/\n" +
            ".gradle\n" +
            ".gradletasknamecache\n" +
            "gradle-app.setting\n" +
            "!gradle-wrapper.jar\n" +
            "\n" +
            "##############################\n" +
            "## IntelliJ\n" +
            "##############################\n" +
            "out/\n" +
            ".idea/\n" +
            ".idea_modules/\n" +
            "*.iml\n" +
            "*.ipr\n" +
            "*.iws\n" +
            "\n" +
            "##############################\n" +
            "## Eclipse\n" +
            "##############################\n" +
            ".settings/\n" +
            "bin/\n" +
            "tmp/\n" +
            ".metadata\n" +
            ".classpath\n" +
            ".project\n" +
            "*.tmp\n" +
            "*.bak\n" +
            "*.swp\n" +
            "*~.nib\n" +
            "local.properties\n" +
            ".loadpath\n" +
            "\n" +
            "##############################\n" +
            "## NetBeans\n" +
            "##############################\n" +
            "nbproject/private/\n" +
            "build/\n" +
            "nbbuild/\n" +
            "dist/\n" +
            "nbdist/\n" +
            "nbactions.xml\n" +
            "nb-configuration.xml\n" +
            "\n" +
            "##############################\n" +
            "## VS Code\n" +
            "##############################\n" +
            ".vscode/\n" +
            "\n" +
            "##############################\n" +
            "## OS X\n" +
            "##############################\n" +
            ".DS_Store";



    public static final String readmeContent = "# Proyecto Base Clean Architecture";
    public static final String testJava = "src/test/java";
    public static final String testResource = "src/test/resources";
}
