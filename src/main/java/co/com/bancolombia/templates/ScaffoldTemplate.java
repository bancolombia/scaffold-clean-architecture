package co.com.bancolombia.templates;

public class ScaffoldTemplate {

    public static final String APPLICATION_PROPERTIES = "application.yaml";
    public static final String LOMBOK_CONFIG = "lombok.config";
    public static final String GRADLE_PROPERTIES = "gradle.properties";
    public static final String MAIN_APPLICATION = "MainApplication.java";
    public static final String DEPLOYMENT = "deployment";
    public static final String DOCKERFILE = "Dockerfile";
    public static final String LOG_4_J = "log4j2.properties";
    public static final String GITIGNORE = ".gitignore";
    public static final String READ_ME = "Readme.md";

    public static final String LOMBOK_CONFIG_CONTENT = "lombok.addLombokGeneratedAnnotation = true";

    public static final String GIT_IGNORE_CONTENT = "##############################\n" +
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
            "### Code-Java ###\n" +
            ".project\n" +
            ".classpath\n" +
            "factoryConfiguration.json\n" +
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

    public static final String README_CONTENT = "# Proyecto Base Implementando Clean Architecture\n" +
            "\n" +
            "## Antes de Iniciar\n" +
            "\n" +
            "Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.\n" +
            "\n" +
            "## Infrastructure\n" +
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

    public static final String LOG_4_J_CONTENT = "name=PropertiesConfig\n" +
            "property.filename = logs\n" +
            "appenders = console\n" +
            "\n" +
            "appender.console.type = Console\n" +
            "appender.console.name = STDOUT\n" +
            "appender.console.layout.type = PatternLayout\n" +
            "appender.console.layout.pattern = [%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n\n" +
            "\n" +
            "\n" +
            "rootLogger.level = debug\n" +
            "rootLogger.appenderRefs = stdout\n" +
            "rootLogger.appenderRef.stdout.ref = STDOUT";

    public static final String DOCKER_FILE_CONTENT = "FROM adoptopenjdk/openjdk8-openj9:alpine-slim\n" +
            "VOLUME /tmp\n" +
            "COPY *.jar app.jar\n" +
            "RUN sh -c 'touch /app.jar'\n" +
            "ENV JAVA_OPTS=\" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom\"\n" +
            "ENTRYPOINT [ \"sh\", \"-c\", \"java $JAVA_OPTS  -jar /app.jar\" ]";

    private ScaffoldTemplate() {
    }

    public static String getMainGradleContent(String type) {
        String value = "subprojects {\n" +
                "    apply plugin: \"java\"\n" +
                "    apply plugin: \"jacoco\"\n" +
                "    apply plugin: 'io.spring.dependency-management'\n" +
                "\n" +
                "    sourceCompatibility = JavaVersion.VERSION_1_8\n" +
                "\n" +
                "    repositories {\n" +
                "  \t\t mavenCentral()\n" +
                "         maven { url \"https://repo.spring.io/snapshot\" }\n" +
                "         maven { url \"https://repo.spring.io/milestone\" }\n" +
                "    }\n" +
                "\n" +
                "    dependencies {\n" +
                "        testImplementation 'org.springframework.boot:spring-boot-starter-test'\n";
        if (!type.equals("imperative")) {
            value = value.concat("\n        testImplementation 'io.projectreactor:reactor-test'\n" +
                    "        implementation 'io.projectreactor:reactor-core'\n" +
                    "        implementation 'io.projectreactor.addons:reactor-extra'\n");
        }
        value = value.concat("\n" +
                "        compileOnly 'org.projectlombok:lombok'\n" +
                "        annotationProcessor 'org.projectlombok:lombok'\n" +
                "        testAnnotationProcessor 'org.projectlombok:lombok'\n" +
                "        testCompileOnly 'org.projectlombok:lombok'\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    jacoco {\n" +
                "        toolVersion = '0.8.2'\n" +
                "    }\n" +
                "\n" +
                "    dependencyManagement {\n" +
                "        imports {\n" +
                "            mavenBom \"org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}\"\n" +
                "            mavenBom \"org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}\"\n" +
                "        }\n" +
                "    }\n" +
                "}");
        return value;


    }

    public static String getBuildGradleApplicationContent(String type) {
        String value = "apply plugin: 'org.springframework.boot'\n" +
                "\n" +
                "\n" +
                "\n" +
                "dependencies {\n" +
                "    compile 'org.springframework.boot:spring-boot-starter'\n" +
                "implementation project(':model')\n" +
                "implementation project(':usecase')\n" +
                "\n";
        if (type.equals("reactive")) {
            value = value.concat("\tcompile 'org.reactivecommons.utils:object-mapper:0.1.0'\n");
        }
        value = value.concat("runtime('org.springframework.boot:spring-boot-devtools')\n" +
                "}\n" +
                "jar {\n" +
                "    archivesBaseName = rootProject.name\n" +
                "    libsDirName = project(\":\").getBuildDir()\n" +
                "}");
        return value;

    }

    public static String getSettingsGradleContent(String nameProject) {
        return "rootProject.name = '" + nameProject + "'\n" +
                "\n" +
                "include \":app-service\"\n" +
                "include \":model\"\n" +
                "include \":usecase\"" +
                "\n" +
                "project(':app-service').projectDir = file('./applications/app-service')\n" +
                "project(':model').projectDir = file('./domain/model')\n" +
                "project(':usecase').projectDir = file('./domain/usecase')";

    }

    public static String getApplicationPropertiesContent(String nameProject) {
        return "##Spring Configuration\n" +
                "server.port=8080\n" +
                "spring.application.name=" + nameProject;
    }

    public static String getMainApplicationContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + ";\n" +
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

    public static String getBuildGradleContent() {
        return "buildscript {\n" +
                "\text {\n" +
                "\t\tspringBootVersion = '2.1.1.RELEASE'\n" +
                "\t\tspringCloudVersion = 'Greenwich.M1'\n" +
                "\t}\n" +
                "\trepositories {\n" +
                "\t\tmavenCentral()\n" +
                "\t\tmaven { url \"https://repo.spring.io/snapshot\" }\n" +
                "\t\tmaven { url \"https://repo.spring.io/milestone\" }\n" +
                "\t}\n" +
                "\tdependencies {\n" +
                "\t\tclasspath(\"org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}\")\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "plugins {\n" +
                "\tid \"org.sonarqube\" version \"2.6\"\n" +
                "\tid \"co.com.bancolombia.cleanArchitecture\" version \"" + PluginTemplate.VERSION_PLUGIN + "\"\n" +
                "}\n" +
                "subprojects {\n" +
                "  apply plugin: \"java\"\n" +
                "  apply plugin: \"jacoco\"\n" +
                "    // Disable the test report for the individual test task\n" +
                "    test {\n" +
                "      reports.html.enabled = false\n" +
                "    }\n" +
                "}" +
                "\n" +
                "apply from: './main.gradle'";
    }

    public static String getGradlePropertiesContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package=" + packageName +
                "\n" +
                "systemProp.version=" + PluginTemplate.VERSION_PLUGIN;
    }
}
