package co.com.bancolombia;


import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String VERSION_PLUGIN = "0.53";
    public static final String JAVA_EXTENSION = ".java";

    /**
     * Base Dirs
     **/
    public static final String INFRAESTUCTURE = "infraestucture";
    public static final String DOMAIN = "domain";
    public static final String APPLICATION = "applications/app-service";

    /**
     * Child Dirs applications
     **/
    public static final String MAIN_JAVA = "src/main/java";
    public static final String MAIN_RESOURCES = "src/main/resources";
    public static final String CONFIG = "config";

    /**
     * Child Dirs Infraestructure
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
            "    compile project(':domain-model')\n" +
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

    public static final String MAIN_GRADLE_CONTENT = "subprojects {\n" +
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
            "        testImplementation 'org.springframework.boot:spring-boot-starter-test'\n" +
            "\n" +
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
            "}";


    public static final String BUILD_GRADLE_APPLICATION_CONTENT = "apply plugin: 'org.springframework.boot'\n" +
            "\n" +
            "\n" +
            "\n" +
            "dependencies {\n" +
            "    compile 'org.springframework.boot:spring-boot-starter'\n" +
            "    compile project(\":domain-usecase\")\n" +
            "\n" +
            "    runtime('org.springframework.boot:spring-boot-devtools')\n" +
            "}\n";


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

    public static final String TEST_JAVA = "src/test/java";

    public static final String AUTHORIZATION_CONFIG = "AuthorizationConfig";

    public static String getSettingsGradleContent(String nameProject) {
        return "rootProject.name = '" + nameProject + "'\n" +
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
                "\t\t//maven { url \"https://artifactory.apps.bancolombia.com:443/maven-bancolombia\" }\n" +
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

    public static String getbuildGradleApplicationContent() {

        return "apply plugin: 'org.springframework.boot'\n" +
                "\n" +
                "\n" +
                "\n" +
                "dependencies {\n" +
                "    compile 'org.springframework.boot:spring-boot-starter'\n" +
                "    compile project(\":domain-usecase\")\n" +
                "\n" +
                "    runtime('org.springframework.boot:spring-boot-devtools')\n" +
                "}\n" +
                "jar {\n" +
                "    archivesBaseName = rootProject.name\n" +
                "    libsDirName = project(\":\").getBuildDir()\n" +
                "}\n";
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
              "   implementation 'org.springframework.boot:spring-boot-starter-web'\n" +
              "    implementation 'org.springframework.boot:spring-boot-starter-security'\n" +
              "    implementation 'com.microsoft.azure:azure-active-directory-spring-boot-starter'\n" +
              "}";

    }

    public static String getAuthorizationConfigApiRest(String packageName) {
        return "package " + packageName + ";\n" +
                "\n" +
                "import org.springframework.boot.web.servlet.FilterRegistrationBean;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.core.Ordered;\n" +
                "import org.springframework.http.HttpMethod;\n" +
                "import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;\n" +
                "import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n" +
                "import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;\n" +
                "import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;\n" +
                "import org.springframework.security.config.http.SessionCreationPolicy;\n" +
                "import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;\n" +
                "import org.springframework.security.web.csrf.CookieCsrfTokenRepository;\n" +
                "import org.springframework.security.web.util.matcher.AntPathRequestMatcher;\n" +
                "import org.springframework.web.cors.CorsConfiguration;\n" +
                "import org.springframework.web.cors.UrlBasedCorsConfigurationSource;\n" +
                "import org.springframework.web.filter.CorsFilter;\n" +
                "\n" +
                "import java.util.Arrays;\n" +
                "\n" +
                "@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)\n" +
                "@EnableWebSecurity\n" +
                "public class AuthorizationConfig extends WebSecurityConfigurerAdapter {\n" +
                "\n" +
                "    @Override\n" +
                "    protected void configure(HttpSecurity http) throws Exception {\n" +
                "        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);\n" +
                "        http.authorizeRequests().antMatchers(HttpMethod.POST, \"/home\").permitAll();\n" +
                "        http.authorizeRequests().antMatchers(\"/api/**\").authenticated();\n" +
                "\n" +
                "        http.logout().logoutRequestMatcher(new AntPathRequestMatcher(\"/logout\"))\n" +
                "                .logoutSuccessUrl(\"/\").deleteCookies(\"JSESSIONID\").invalidateHttpSession(true);\n" +
                "\n" +
                "        http.authorizeRequests().anyRequest().permitAll();\n" +
                "\n" +
                "        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());\n" +
                "        http.csrf().disable();\n" +
                "    }\n" +
                "\n" +
                "    @Bean\n" +
                "    public FilterRegistrationBean corsFilter() {\n" +
                "        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();\n" +
                "        CorsConfiguration config = new CorsConfiguration();\n" +
                "        config.setAllowCredentials(true);\n" +
                "        config.addAllowedOrigin(\"http://localhost:4200\");\n" +
                "        config.setAllowedMethods(Arrays.asList(\"POST\", \"OPTIONS\", \"GET\", \"DELETE\", \"PUT\"));\n" +
                "        config.setAllowedHeaders(Arrays.asList(\"X-Requested-With\", \"Origin\", \"Content-Type\", \"Accept\", \"Authorization\"));\n" +
                "        source.registerCorsConfiguration(\"/**\", config);\n" +
                "        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));\n" +
                "        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);\n" +
                "        return bean;\n" +
                "    }\n" +
                "}";
    }
}
