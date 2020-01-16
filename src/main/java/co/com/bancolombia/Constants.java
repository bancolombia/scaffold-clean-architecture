package co.com.bancolombia;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String COMMON = "common";
    public static final String SECRET_MANAGER_CONSUMER_CLASS = "SecretsManagerConsumer";

    private Constants() {
    }
    public static final String GENERATING_CHILDS_DIRS = "Generating Childs Dirs";
    public static final String GENERATED_CHILDS_DIRS = "Generated Childs Dirs";
    public static final String WRITING_IN_FILES = "Writing in Files";
    public static final String WRITED_IN_FILES = "Writed in Files";

    public static final String VERSION_PLUGIN = "1.3";
    public static final String JAVA_EXTENSION = ".java";

    /**
     * Base Dirs
     **/
    public static final String INFRASTRUCTURE = "infrastructure";
    public static final String DOMAIN = "domain";
    public static final String APPLICATION = "applications/app-service";
    public static final String DEPLOYMENT = "deployment";
    public static final String DOCKERFILE = "Dockerfile";

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
    public static final String GRADLE_PROPERTIES = "gradle.properties";
    public static final String APPLICATION_PROPERTIES = "application.yaml";
    public static final String MAIN_APPLICATION = "MainApplication.java";

    public static final String LOG_4_J = "log4j2.properties";
    public static final String MAIN_GRADLE = "main.gradle";
    public static final String LOMBOK_CONFIG = "lombok.config";
    public static final String SETTINGS_GRADLE = "settings.gradle";
    public static final String GITIGNORE = ".gitignore";
    public static final String READ_ME = "Readme.md";

    private static final Map<Integer, String> ENTRY_POINTS_AVAILABLE = new HashMap<>();
    private static final Map<Integer, String> DRIVEN_ADAPTERS_AVAILABLE = new HashMap<>();


    public static final String BUILD_GRADLE_USE_CASE_CONTENT = "dependencies {\n" +
            "    implementation project(':model')\n" +
            "}";

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

    public static String mainGradleContent(String type) {
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

    public static  String buildGradleApplicationContent(String type) {
        String value = "apply plugin: 'org.springframework.boot'\n" +
                "\n" +
                "\n" +
                "\n" +
                "dependencies {\n" +
                "    compile 'org.springframework.boot:spring-boot-starter'\n" +
                "implementation project(':model')\n" +
                "implementation project(':usecase')\n" +
                "\n" ;
        if (type.equals("reactive")){
            value = value.concat("\tcompile 'org.reactivecommons.utils:object-mapper:0.1.0'\n");
        }
        value = value.concat( "runtime('org.springframework.boot:spring-boot-devtools')\n" +
                "}\n" +
                "jar {\n" +
                "    archivesBaseName = rootProject.name\n" +
                "    libsDirName = project(\":\").getBuildDir()\n" +
                "}");
        return value;

    }


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


    public static final String API_REST_CLASS = "ApiRest";
    public static final String SECRET_MANAGER_CLASS = "SecretsManager";
    public static final String MONGO_REPOSITORY_CLASS = "MongoRepositoryAdapter";
    public static final String MONGO_REPOSITORY_INTERFACE = "IMongoRepository";
    public static final String JPA_REPOSITORY_CLASS = "JPARepositoryAdapter";
    public static final String JPA_REPOSITORY_INTERFACE = "JPARepository";
    public static final String MONGO_HELPER_CLASS = "AdapterOperations";
    public static final String JPA_HELPER_CLASS = "AdapterOperations";
    public static final String DOCKER_FILE_CONTENT = "FROM adoptopenjdk/openjdk8-openj9:alpine-slim\n" +
            "VOLUME /tmp\n" +
            "COPY *.jar app.jar\n" +
            "RUN sh -c 'touch /app.jar'\n" +
            "ENV JAVA_OPTS=\" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom\"\n" +
            "ENTRYPOINT [ \"sh\", \"-c\", \"java $JAVA_OPTS  -jar /app.jar\" ]";

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

    public static String getSettingsSecretsManagerContent() {
        return "\ninclude \":secrets-manager\"\n" +
                "project(':secrets-manager').projectDir = file('./infrastructure/driven-adapters/secrets-manager-consumer')\n";
    }

    public static String getSettingsJPARepositoryContent() {
        return "\ninclude \":jpa-repository\"\n" +
                "project(':jpa-repository').projectDir = file('./infrastructure/driven-adapters/jpa-repository')\n";
    }

    public static String getSettingsHelperJPAContent() {
        return "\ninclude \":jpa-repository-commons\"\n" +
                "project(':jpa-repository-commons').projectDir = file('./infrastructure/helpers/jpa-repository-commons')\n";
    }

    public static String getSettingsMongoRepositoryContent() {
        return "\ninclude \":mongo-repository\"\n" +
                "project(':mongo-repository').projectDir = file('./infrastructure/driven-adapters/mongo-repository')\n";
    }

    public static String getSettingsHelperMongoContent() {
        return "\ninclude \":mongo-repository-commons\"\n" +
                "project(':mongo-repository-commons').projectDir = file('./infrastructure/helpers/mongo-repository-commons')\n";
    }


    public static String getSettingsApiRestContent() {
        return "\ninclude \":api-rest\"\n" +
                "project(':api-rest').projectDir = file('./infrastructure/entry-points/api-rest')\n";
    }
    public static String getSettingsReactiveWebContent() {
        return "\ninclude \":reactive-web\"\n" +
                "project(':reactive-web').projectDir = file('./infrastructure/entry-points/reactive-web')\n";
    }

    public static String getApplicationPropertiesContent(String nameProject) {
        return "##Spring Configuration\n" +
                "server.port=8080\n" +
                "spring.application.name=" + nameProject;
    }

    public static String getMainApplicationContent(String nameProject) {
        return "package " + nameProject + ";\n" +
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

    public static String getModel(String modelName, String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + "." + MODEL + "." + Utils.decapitalize(modelName) + ";\n" +
                "\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "\n" +
                "@Data\n" +
                "@Builder(toBuilder = true)\n" +
                "public class " + Utils.capitalize(modelName) + "{\n" +
                "\n" +
                "\n" +
                "}\n";
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
                "\tid \"co.com.bancolombia.cleanArchitecture\" version \"" + VERSION_PLUGIN + "\"\n" +
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

    public static String getInterfaceModel(String modelName, String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + "." + MODEL + "." + Utils.decapitalize(modelName) + "." + GATEWAYS + ";\n" +
                "\n" +
                "import " + packageName + "." + MODEL + "." + Utils.decapitalize(modelName) + "." + Utils.capitalize(modelName) + ";\n" +
                "\n" +
                "public interface " + Utils.capitalize(modelName) + "Repository " + "{\n" +
                "\n" +
                "\n" +
                "}\n";
    }

    public static String getGradlePropertiesContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package=" + packageName +
                "\n" +
                "systemProp.version=" + VERSION_PLUGIN;
    }

    public static String getUseCase(String useCaseName, String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + "." + USECASE + "." + Utils.decapitalize(useCaseName) + ";\n" +
                "\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + Utils.capitalize(useCaseName) + " {\n" +
                "\n" +
                "}\n";
    }

    public static String getNameEntryPoint(int numberEntryPoint) {

        if (ENTRY_POINTS_AVAILABLE.isEmpty()) {
            ENTRY_POINTS_AVAILABLE.put(1, "API REST IMPERATIVE");
            ENTRY_POINTS_AVAILABLE.put(2, "API REST REACTIVE");
        }
        return ENTRY_POINTS_AVAILABLE.get(numberEntryPoint);
    }

    public static String getNameDrivenAdapter(int numberDriverAdapter) {

        if (DRIVEN_ADAPTERS_AVAILABLE.isEmpty()) {
            DRIVEN_ADAPTERS_AVAILABLE.put(1, "JPA REPOSITORY");
            DRIVEN_ADAPTERS_AVAILABLE.put(2, "MONGO REPOSITORY");
            DRIVEN_ADAPTERS_AVAILABLE.put(3, "SECRETS MANAGER CONSUMER");
        }
        return DRIVEN_ADAPTERS_AVAILABLE.get(numberDriverAdapter);
    }


    public static String getBuildGradleApiRest() {

        return "dependencies {\n" +
                "    implementation project(':usecase')\n" +
                "    implementation project(':model')\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-web'\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-security'\n" +
                "}";

    }

    public static String getBuildGradleReactiveWeb() {

        return "dependencies {\n" +
                "    implementation project(':usecase')\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-webflux'\n" +
                "}";

    }

    public static String getBuildGradleSecretsManager() {

        return "dependencies {\n" +
                "    implementation project(':model')\n" +
                "    implementation 'org.springframework:spring-context:2.0.5'\n" +
                "    implementation 'co.bia:secretsmanager:2.0.1'\n" +
                "}";

    }

    public static String getBuildGradleMongoRepository() {

        return "dependencies {\n" +
                "    implementation project(':model')\n" +
                "    implementation project(':mongo-repository-commons')\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'\n" +
                "    implementation 'org.springframework:spring-context:5.2.0.RELEASE'\n" +
                "    implementation 'javax.persistence:javax.persistence-api:2.2'\n" +
                "}";
    }

    public static String getBuildGradleJPARepository() {

        return "dependencies {\n" +
                "    compile 'org.springframework.boot:spring-boot-starter-data-jpa'\n" +
                "    compile 'org.reactivecommons.utils:object-mapper-api:0.1.0'\n" +
                "    compile 'org.apache.commons:commons-dbcp2:2.2.0'\n" +
                "\n" +
                "    testCompile 'org.reactivecommons.utils:object-mapper:0.1.0'\n" +
                "}";

    }

    public static String getBuildGradleHelperJPARepository() {

        return "dependencies {\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'\n" +
                "    compile 'org.reactivecommons.utils:object-mapper-api:0.1.0'\n" +
                "}";

    }

    public static String getBuildGradleHelperMongoRepository() {

        return "dependencies {\n" +
                "  implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'\n" +
                "    compile 'org.reactivecommons.utils:object-mapper-api:0.1.0'\n" +
                "}";

    }

    public static String getApiRestClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "import org.springframework.http.HttpHeaders;\n" +
                "import org.springframework.http.HttpStatus;\n" +
                "import org.springframework.http.MediaType;\n" +
                "import org.springframework.http.ResponseEntity;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "import org.springframework.web.context.request.WebRequest;\n" +
                "\n" +
                "import java.nio.file.AccessDeniedException;\n" +
                "\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(value = \"/api\", produces = MediaType.APPLICATION_JSON_VALUE)\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + API_REST_CLASS + " {\n" +
                "\n" +
                "    private final Object useCase;\n" +
                "\n" +
                "    @GetMapping (path = \"/health\")\n" +
                "    public Object health() {\n" +
                "        return useCase;\n" +
                "    }\n" +
                "\n" +
                "    @ExceptionHandler({ AccessDeniedException.class })\n" +
                "    public ResponseEntity<Object> handleAccessDeniedException(\n" +
                "            Exception ex, WebRequest request) {\n" +
                "        return new ResponseEntity<Object>(\n" +
                "                \"Access denied message here\", new HttpHeaders(), HttpStatus.FORBIDDEN);\n" +
                "    }" +
                "}";
    }

    public static String getReactiveWebClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "import org.springframework.http.MediaType;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "import reactor.core.publisher.Mono;\n"+
                "\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(value = \"/api\", produces = MediaType.APPLICATION_JSON_VALUE)\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + API_REST_CLASS + " {\n" +
                "\n" +
                "    private final Object useCase;\n" +
                "\n" +
                "    @GetMapping (path = \"/health\")\n" +
                "    public Mono<Object> health() {\n" +
                "        return Mono.empty();\n" +
                "    }\n" +
                "\n"+
                "}";
    }

    public static String getSecretsManagerClassContent(String packageName, String drivenAdapterPackage) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + "." + drivenAdapterPackage + ";\n" +
                "\n" +
                "import " + packageName + "." + COMMON + "." + GATEWAYS + "." + SECRET_MANAGER_CONSUMER_CLASS + ";\n" +
                "import co.com.bancolombia.commons.secretsmanager.connector.AbstractConnector;\n" +
                "import co.com.bancolombia.commons.secretsmanager.connector.clients.AWSSecretManagerConnector;\n" +
                "import co.com.bancolombia.commons.secretsmanager.exceptions.SecretException;\n" +
                "import co.com.bancolombia.commons.secretsmanager.manager.GenericManager;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "\n" +
                "@Repository\n" +
                "public class " + SECRET_MANAGER_CLASS + " implements " + SECRET_MANAGER_CONSUMER_CLASS + " {\n" +
                "\n" +
                "\n" +
                "    public " + SECRET_MANAGER_CLASS + "() {\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Object getSecrets(Class cls, String secretRegion, String secretName) throws SecretException {\n" +
                "        AbstractConnector connector = new AWSSecretManagerConnector(secretRegion);\n" +
                "        GenericManager manager = new GenericManager(connector);\n" +
                "        return manager.getSecretModel(secretName, cls);\n" +
                "    }\n" +
                "}";
    }

    public static String getMongoRepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Repository\n" +
                "public class " + MONGO_REPOSITORY_CLASS + " extends AdapterOperations<Object, Object, String, " + MONGO_REPOSITORY_INTERFACE + "> " +
                "/**        implements <INTERFACE DOMAIN> **/" +
                "\n{\n" +
                "\n" +
                "    @Autowired\n" +
                "    public " + MONGO_REPOSITORY_CLASS + "(" + MONGO_REPOSITORY_INTERFACE + " repository, ObjectMapper mapper) {\n" +
                "        super(repository, mapper, d -> mapper.mapBuilder(d, Object.class));\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    public static String getJPARepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Repository\n" +
                "public class " + JPA_REPOSITORY_CLASS + " extends AdapterOperations<Object, Object, String," + JPA_REPOSITORY_INTERFACE + ">\n" +
                "/**        implements <INTERFACE DOMAIN> **/" +
                "\n{\n" +
                "\n" +
                "    @Autowired\n" +
                "    public " + JPA_REPOSITORY_CLASS + "(" + JPA_REPOSITORY_INTERFACE + " repository, ObjectMapper mapper) {\n" +
                "        super(repository, mapper, d -> mapper.mapBuilder(d, Object.class));\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    public static String getJPARepositoryInterfaceContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.springframework.data.jpa.repository.Query;\n" +
                "import org.springframework.data.repository.CrudRepository;\n" +
                "import org.springframework.data.repository.query.Param;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public interface " + JPA_REPOSITORY_INTERFACE + " extends CrudRepository<Object, String>, QueryByExampleExecutor<Object> {\n" +
                "}";
    }

    public static String getMongoRepositoryInterfaceContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.springframework.data.mongodb.repository.MongoRepository;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "public interface " + MONGO_REPOSITORY_INTERFACE + " extends MongoRepository<Object, String> , QueryByExampleExecutor<Object> {\n" +
                "}";
    }

    public static String getHelperJPARepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.data.domain.Example;\n" +
                "import org.springframework.data.repository.CrudRepository;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "import java.lang.reflect.ParameterizedType;\n" +
                "import java.util.List;\n" +
                "import java.util.function.Function;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import static java.util.stream.StreamSupport.stream;\n" +
                "\n" +
                "public abstract class " + JPA_HELPER_CLASS + "<E, D, I, R extends CrudRepository<D, I> & QueryByExampleExecutor<D>> {\n" +
                "\n" +
                "\n" +
                "    protected R repository;\n" +
                "    private Class<D> dataClass;\n" +
                "    protected ObjectMapper mapper;\n" +
                "    private Function<D, E> toEntityFn;\n" +
                "\n" +
                "    public " + JPA_HELPER_CLASS + "(R repository, ObjectMapper mapper, Function<D, E> toEntityFn) {\n" +
                "        this.repository = repository;\n" +
                "        this.mapper = mapper;\n" +
                "        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();\n" +
                "        this.dataClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];\n" +
                "        this.toEntityFn = toEntityFn;\n" +
                "    }\n" +
                "\n" +
                "    protected D toData(E entity) {\n" +
                "        return mapper.map(entity, dataClass);\n" +
                "    }\n" +
                "\n" +
                "    protected E toEntity(D data) {\n" +
                "        return data != null ? toEntityFn.apply(data) : null;\n" +
                "    }\n" +
                "\n" +
                "    public E save(E entity) {\n" +
                "        D data = toData(entity);\n" +
                "        return toEntity(saveData(data));\n" +
                "    }\n" +
                "\n" +
                "    protected List<E> saveAllEntities(List<E> entities) {\n" +
                "        List<D> list = entities.stream().map(this::toData).collect(Collectors.toList());\n" +
                "        return toList(saveData(list));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> toList(Iterable<D> iterable) {\n" +
                "        return stream(iterable.spliterator(), false).map(this::toEntity).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    protected D saveData(D data) {\n" +
                "        return repository.save(data);\n" +
                "    }\n" +
                "\n" +
                "    protected Iterable<D> saveData(List<D> data) {\n" +
                "        return repository.saveAll(data);\n" +
                "    }\n" +
                "\n" +
                "    public E findById(I id) {\n" +
                "        return toEntity(repository.findById(id).orElse(null));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> findByExample(E entity) {\n" +
                "        return toList(repository.findAll( Example.of(toData(entity))));\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public List<E> findAll(){\n" +
                "        return toList(repository.findAll());\n" +
                "    }\n" +
                "}";
    }

    public static String getHelperMongoRepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.data.domain.Example;\n" +
                "import org.springframework.data.mongodb.repository.MongoRepository;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "import java.lang.reflect.ParameterizedType;\n" +
                "import java.util.List;\n" +
                "import java.util.function.Function;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import static java.util.stream.StreamSupport.stream;\n" +
                "\n" +
                "public abstract class " + MONGO_HELPER_CLASS + "<E, D, I, R extends MongoRepository<D, I> & QueryByExampleExecutor<D>> {\n" +
                "\n" +
                "    protected R repository;\n" +
                "    private Class<D> dataClass;\n" +
                "    protected ObjectMapper mapper;\n" +
                "    private Function<D, E> toEntityFn;\n" +
                "\n" +
                "    public " + MONGO_HELPER_CLASS + "(R repository, ObjectMapper mapper, Function<D, E> toEntityFn) {\n" +
                "        this.repository = repository;\n" +
                "        this.mapper = mapper;\n" +
                "        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();\n" +
                "        this.dataClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];\n" +
                "        this.toEntityFn = toEntityFn;\n" +
                "    }\n" +
                "\n" +
                "    protected D toData(E entity) {\n" +
                "        return mapper.map(entity, dataClass);\n" +
                "    }\n" +
                "\n" +
                "    protected E toEntity(D data) {\n" +
                "        return data != null ? toEntityFn.apply(data) : null;\n" +
                "    }\n" +
                "\n" +
                "    public E save(E entity) {\n" +
                "        D data = toData(entity);\n" +
                "        return toEntity(saveData(data));\n" +
                "    }\n" +
                "\n" +
                "    protected List<E> saveAllEntities(List<E> entities) {\n" +
                "        List<D> list = entities.stream().map(this::toData).collect(Collectors.toList());\n" +
                "        return toList(saveData(list));\n" +
                "    }\n" +
                "\n" +
                "    private List<E> toList(Iterable<D> iterable) {\n" +
                "        return stream(iterable.spliterator(), false).map(this::toEntity).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    private D saveData(D data) {\n" +
                "        return repository.save(data);\n" +
                "    }\n" +
                "\n" +
                "    protected Iterable<D> saveData(List<D> data) {\n" +
                "        return repository.saveAll(data);\n" +
                "    }\n" +
                "\n" +
                "    public E findById(I id) {\n" +
                "        return toEntity(repository.findById(id).orElse(null));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> findByExample(E entity) {\n" +
                "        return toList(repository.findAll( Example.of(toData(entity))));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> findAll(){\n" +
                "        return toList(repository.findAll());\n" +
                "    }\n" +
                "}";
    }


    public static String getSecretsManagerInterfaceContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "public interface " + SECRET_MANAGER_CONSUMER_CLASS + "<T> {\n" +
                "    T getSecrets(Class<T> cls, String secretRegion, String secretName) throws Exception;\n" +
                "}";
    }
}
