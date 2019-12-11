package co.com.bancolombia;

public class Constants {

    public static final String extensionJava = ".java";

    /**
     * Base Dirs
     **/
    public static final String infraestucture = "infraestucture";
    public static final String domain = "domain";
    public static final String application = "applications/app-service";

    /**
     * Child Dirs applications
     **/
    public static final String mainJava = "src/main/java";
    public static final String mainResource = "src/main/resources";
    public static final String config = "config";

    /**
     * Child Dirs Infraestructure
     **/

    public static final String drivenAdapters = "driven-adapters";
    public static final String entryPoints = "entry-points";
    public static final String helpers = "helpers";

    /**
     * Child Dirs Domain
     **/

    public static final String model = "model";
    public static final String gateway = "gateways";
    public static final String repository = "Repository";
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


    public static final String readmeContent = "# Proyecto Base Implementando Clean Architecture\n" +
            "\n" +
            "## Antes de Iniciar\n" +
            "\n" +
            "Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.\n" +
            "\n" +
            "## Infraestructure\n" +
            "\n" +
            "### Helpers\n" +
            "En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.\n" +
            "\n" +
            "Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006) \n" +
            "\n" +
            "Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**\n" +
            "\n" +
            "## Driven Adapters\n" +
            "Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest, soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos interactuar.\n" +
            "\n" +
            "## Entry Points\n" +
            "Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.";

    public static final String mainGradleContent ="subprojects {\n"+
        "    apply plugin: \"java\"\n"+
        "    apply plugin: \"jacoco\"\n"+
        "    apply plugin: 'io.spring.dependency-management'\n"+
        "\n"+
        "    sourceCompatibility = JavaVersion.VERSION_1_8\n"+
        "\n"+
        "    repositories {\n"+
        "  \t\t mavenCentral()\n"+
        "         maven { url \"https://repo.spring.io/snapshot\" }\n"+
        "         maven { url \"https://repo.spring.io/milestone\" }\n"+
        "  \t\t//maven { url \"https://artifactory.apps.bancolombia.com:443/maven-bancolombia\" }\n"+
        "    }\n"+
        "\n"+
        "    dependencies {\n"+
        "        testImplementation 'org.springframework.boot:spring-boot-starter-test'\n"+
        "\n"+
        "        compileOnly 'org.projectlombok:lombok'\n"+
        "        annotationProcessor 'org.projectlombok:lombok'\n"+
        "        testAnnotationProcessor 'org.projectlombok:lombok'\n"+
        "        testCompileOnly 'org.projectlombok:lombok'\n"+
        "    }\n"+
        "\n"+
        "\n"+
        "    jacoco {\n"+
        "        toolVersion = '0.8.2'\n"+
        "    }\n"+
        "\n"+
        "    dependencyManagement {\n"+
        "        imports {\n"+
        "            mavenBom \"org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}\"\n"+
        "            mavenBom \"org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}\"\n"+
        "        }\n"+
        "    }\n"+
        "}";
    public static final String buildGradleContent = "buildscript {\n" +
            "\text {\n" +
            "\t\tspringBootVersion = '2.1.1.RELEASE'\n" +
            "\t\tspringCloudVersion = 'Greenwich.M1'\n" +
            "\t}\n" +
            "\trepositories {\n" +
            "\t\tmavenCentral()\n" +
            "\t\tmaven { url \"https://repo.spring.io/snapshot\" }\n" +
            "\t\tmaven { url \"https://repo.spring.io/milestone\" }\n" +
            "\t\t//maven { url \"https://artifactory.apps.bancolombia.com:443/maven-bancolombia\" }\n" +
            "\t}\n" +
            "\tdependencies {\n" +
            "\t\tclasspath(\"org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}\")\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "plugins {\n" +
            "\tid \"org.sonarqube\" version \"2.6\"\n" +
            "\tid \"co.com.bancolombia.cleanArchitecture\" version \"0.33\"\n" +
            "}\n" +
            "\n" +
            "task testReport(type: TestReport) {\n" +
            "    destinationDir = file(\"$buildDir/reports/allTests\")\n" +
            "    // Include the results from the `test` task in all subprojects\n" +
            "    reportOn subprojects*.test\n" +
            "}\n"+
            "apply from: './main.gradle'";
    public static final String buildGradleApplicationContent ="apply plugin: 'org.springframework.boot'\n"+
        "\n"+
        "\n"+
        "\n"+
        "dependencies {\n"+
        "    compile 'org.springframework.boot:spring-boot-starter'\n"+
        "    compile project(\":domain-usecase\")\n"+
        "\n"+
        "    runtime('org.springframework.boot:spring-boot-devtools')\n"+
        "}\n";

    public static final String log4jContent ="name=PropertiesConfig\n"+
            "property.filename = logs\n"+
            "appenders = console\n"+
            "\n"+
            "appender.console.type = Console\n"+
            "appender.console.name = STDOUT\n"+
            "appender.console.layout.type = PatternLayout\n"+
            "appender.console.layout.pattern = [%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n\n"+
            "\n"+
            "\n"+
            "rootLogger.level = debug\n"+
            "rootLogger.appenderRefs = stdout\n"+
            "rootLogger.appenderRef.stdout.ref = STDOUT";
    public static final String testJava = "src/test/java";

    public static String getSettingsGradleContent(String nameProject) {
        return "rootProject.name = '"+ nameProject +"'\n" +
                "\n" +
                "FileTree buildFiles = fileTree(rootDir) {\n" +
                "    List excludes = gradle.startParameter.projectProperties.get(\"excludeProjects\")?.split(\",\")\n" +
                "    include '**/*.gradle'\n" +
                "    exclude 'applications/admin-server', 'main.gradle','build', '**/gradle', 'settings.gradle', 'buildSrc', '/build.gradle', '.*', 'out'\n" +
                "    if(excludes) {\n" +
                "        exclude excludes\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "String rootDirPath = rootDir.absolutePath + File.separator;\n" +
                "buildFiles.each { File buildFile ->\n" +
                "\n" +
                "    boolean isDefaultName = 'build.gradle'.equals(buildFile.name)\n" +
                "    if(isDefaultName) {\n" +
                "        String projectName = buildFile.parentFile.name\n" +
                "        String projectPath = ':' + projectName\n" +
                "        include projectPath\n" +
                "        def project = findProject(\"${projectPath}\")\n" +
                "        project.name = buildFile.parentFile.parentFile.name + '-' + projectName\n" +
                "        project.projectDir = buildFile.parentFile\n" +
                "        project.buildFileName = buildFile.name\n" +
                "\n" +
                "    } else {\n" +
                "        String projectName = buildFile.name.replace('.gradle', '')\n" +
                "        String projectPath = ':' + projectName\n" +
                "        include projectPath\n" +
                "        def project = findProject(\"${projectPath}\")\n" +
                "        project.name = projectName\n" +
                "        project.projectDir = buildFile.parentFile\n" +
                "        project.buildFileName = buildFile.name\n" +
                "    }\n" +
                "}";
    }

    public static String getApplicationPropertiesContent(String nameProject) {
        return "##Spring Configuration\n" +
                "server.port=8080\n" +
                "spring.application.name="+nameProject;
    }
    public static String getMainApplicationContent(String nameProject){
        return "package "+ nameProject + ";\n" +
                "\n" +
                "import org.springframework.boot.SpringApplication;\n" +
                "import org.springframework.boot.autoconfigure.EnableAutoConfiguration;\n" +
                "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
                "\n" +
                "@SpringBootApplication\n" +
                "@EnableAutoConfiguration()\n" +
                "public class MainApplication {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        SpringApplication.run(MainApplication.class, args);\n" +
                "    }\n" +
                "}";
    }

    public static String getModel(String nameModel, String _package) {
        return "";
    }

    public static String getInterfaceModel(String nameModel, String _package) {
        return "package " + _package + "." + domain + "." + nameModel + "." + gateway + ";\n" +
                "\n" +
                "import " + _package + "." + domain + "." + nameModel.toLowerCase() + "." + nameModel + ";\n"+
        "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public interface MovieRepository {\n" +
                "\n" +
                "    Movie save(Movie movie);\n" +
                "    void saveAll(List<Movie> movies);\n" +
                "    Movie findById(String id);\n" +
                "    List<Movie> findAll();\n" +
                "    List<Movie> findMoviesByCategory(String category);\n" +
                "\n" +
                "}\n";
    }
}
