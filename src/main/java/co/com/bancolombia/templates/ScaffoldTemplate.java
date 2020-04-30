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
    public static final String IMPERATIVE_PROJECT = "imperative";
    public static final String SONAR_PLUGIN_VERSION = "2.7";
    public static final String NET_SALIMAN_COBERTURA_VERSION = "3.0.0";

    public static final String LOMBOK_CONFIG_CONTENT = "lombok.addLombokGeneratedAnnotation = true";

    public static final String GIT_IGNORE_CONTENT = "# Created by https://www.gitignore.io/api/java,linux,macos,sonar,gradle,windows,eclipse,sonarqube,sublimetext,intellij+all,visualstudiocode\n" +
            "# Edit at https://www.gitignore.io/?templates=java,linux,macos,sonar,gradle,windows,eclipse,sonarqube,sublimetext,intellij+all,visualstudiocode\n" +
            "\n" +
            "### Eclipse ###\n" +
            ".metadata\n" +
            "bin/\n" +
            "tmp/\n" +
            "*.tmp\n" +
            "*.bak\n" +
            "*.swp\n" +
            "*~.nib\n" +
            "local.properties\n" +
            ".settings/\n" +
            ".loadpath\n" +
            ".recommenders\n" +
            "\n" +
            "# External tool builders\n" +
            ".externalToolBuilders/\n" +
            "\n" +
            "# Locally stored \"Eclipse launch configurations\"\n" +
            "*.launch\n" +
            "\n" +
            "# PyDev specific (Python IDE for Eclipse)\n" +
            "*.pydevproject\n" +
            "\n" +
            "# CDT-specific (C/C++ Development Tooling)\n" +
            ".cproject\n" +
            "\n" +
            "# CDT- autotools\n" +
            ".autotools\n" +
            "\n" +
            "# Java annotation processor (APT)\n" +
            ".factorypath\n" +
            "\n" +
            "# PDT-specific (PHP Development Tools)\n" +
            ".buildpath\n" +
            "\n" +
            "# sbteclipse plugin\n" +
            ".target\n" +
            "\n" +
            "# Tern plugin\n" +
            ".tern-project\n" +
            "\n" +
            "# TeXlipse plugin\n" +
            ".texlipse\n" +
            "\n" +
            "# STS (Spring Tool Suite)\n" +
            ".springBeans\n" +
            "\n" +
            "# Code Recommenders\n" +
            ".recommenders/\n" +
            "\n" +
            "# Annotation Processing\n" +
            ".apt_generated/\n" +
            "\n" +
            "# Scala IDE specific (Scala & Java development for Eclipse)\n" +
            ".cache-main\n" +
            ".scala_dependencies\n" +
            ".worksheet\n" +
            "\n" +
            "### Eclipse Patch ###\n" +
            "# Eclipse Core\n" +
            ".project\n" +
            "\n" +
            "# JDT-specific (Eclipse Java Development Tools)\n" +
            ".classpath\n" +
            "\n" +
            "# Annotation Processing\n" +
            ".apt_generated\n" +
            "\n" +
            ".sts4-cache/\n" +
            "\n" +
            "### Intellij+all ###\n" +
            "# Covers JetBrains IDEs: IntelliJ, RubyMine, PhpStorm, AppCode, PyCharm, CLion, Android Studio and WebStorm\n" +
            "# Reference: https://intellij-support.jetbrains.com/hc/en-us/articles/206544839\n" +
            "\n" +
            "# User-specific stuff\n" +
            ".idea/**/workspace.xml\n" +
            ".idea/**/tasks.xml\n" +
            ".idea/**/usage.statistics.xml\n" +
            ".idea/**/dictionaries\n" +
            ".idea/**/shelf\n" +
            "\n" +
            "# Generated files\n" +
            ".idea/**/contentModel.xml\n" +
            "\n" +
            "# Sensitive or high-churn files\n" +
            ".idea/**/dataSources/\n" +
            ".idea/**/dataSources.ids\n" +
            ".idea/**/dataSources.local.xml\n" +
            ".idea/**/sqlDataSources.xml\n" +
            ".idea/**/dynamic.xml\n" +
            ".idea/**/uiDesigner.xml\n" +
            ".idea/**/dbnavigator.xml\n" +
            "\n" +
            "# Gradle\n" +
            ".idea/**/gradle.xml\n" +
            ".idea/**/libraries\n" +
            "\n" +
            "# Gradle and Maven with auto-import\n" +
            "# When using Gradle or Maven with auto-import, you should exclude module files,\n" +
            "# since they will be recreated, and may cause churn.  Uncomment if using\n" +
            "# auto-import.\n" +
            "# .idea/modules.xml\n" +
            "# .idea/*.iml\n" +
            "# .idea/modules\n" +
            "# *.iml\n" +
            "# *.ipr\n" +
            "\n" +
            "# CMake\n" +
            "cmake-build-*/\n" +
            "\n" +
            "# Mongo Explorer plugin\n" +
            ".idea/**/mongoSettings.xml\n" +
            "\n" +
            "# File-based project format\n" +
            "*.iws\n" +
            "\n" +
            "# IntelliJ\n" +
            "out/\n" +
            "\n" +
            "# mpeltonen/sbt-idea plugin\n" +
            ".idea_modules/\n" +
            "\n" +
            "# JIRA plugin\n" +
            "atlassian-ide-plugin.xml\n" +
            "\n" +
            "# Cursive Clojure plugin\n" +
            ".idea/replstate.xml\n" +
            "\n" +
            "# Crashlytics plugin (for Android Studio and IntelliJ)\n" +
            "com_crashlytics_export_strings.xml\n" +
            "crashlytics.properties\n" +
            "crashlytics-build.properties\n" +
            "fabric.properties\n" +
            "\n" +
            "# Editor-based Rest Client\n" +
            ".idea/httpRequests\n" +
            "\n" +
            "# Android studio 3.1+ serialized cache file\n" +
            ".idea/caches/build_file_checksums.ser\n" +
            "\n" +
            "### Intellij+all Patch ###\n" +
            "# Ignores the whole .idea folder and all .iml files\n" +
            "# See https://github.com/joeblau/gitignore.io/issues/186 and https://github.com/joeblau/gitignore.io/issues/360\n" +
            "\n" +
            ".idea/\n" +
            "\n" +
            "# Reason: https://github.com/joeblau/gitignore.io/issues/186#issuecomment-249601023\n" +
            "\n" +
            "*.iml\n" +
            "modules.xml\n" +
            ".idea/misc.xml\n" +
            "*.ipr\n" +
            "\n" +
            "# Sonarlint plugin\n" +
            ".idea/sonarlint\n" +
            "\n" +
            "### Java ###\n" +
            "# Compiled class file\n" +
            "*.class\n" +
            "\n" +
            "# Log file\n" +
            "*.log\n" +
            "\n" +
            "# BlueJ files\n" +
            "*.ctxt\n" +
            "\n" +
            "# Mobile Tools for Java (J2ME)\n" +
            ".mtj.tmp/\n" +
            "\n" +
            "# Package Files #\n" +
            "*.jar\n" +
            "*.war\n" +
            "*.nar\n" +
            "*.ear\n" +
            "*.zip\n" +
            "*.tar.gz\n" +
            "*.rar\n" +
            "\n" +
            "# virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml\n" +
            "hs_err_pid*\n" +
            "\n" +
            "### Linux ###\n" +
            "*~\n" +
            "\n" +
            "# temporary files which can be created if a process still has a handle open of a deleted file\n" +
            ".fuse_hidden*\n" +
            "\n" +
            "# KDE directory preferences\n" +
            ".directory\n" +
            "\n" +
            "# Linux trash folder which might appear on any partition or disk\n" +
            ".Trash-*\n" +
            "\n" +
            "# .nfs files are created when an open file is removed but is still being accessed\n" +
            ".nfs*\n" +
            "\n" +
            "### macOS ###\n" +
            "# General\n" +
            ".DS_Store\n" +
            ".AppleDouble\n" +
            ".LSOverride\n" +
            "\n" +
            "# Icon must end with two \\r\n" +
            "Icon\n" +
            "\n" +
            "# Thumbnails\n" +
            "._*\n" +
            "\n" +
            "# Files that might appear in the root of a volume\n" +
            ".DocumentRevisions-V100\n" +
            ".fseventsd\n" +
            ".Spotlight-V100\n" +
            ".TemporaryItems\n" +
            ".Trashes\n" +
            ".VolumeIcon.icns\n" +
            ".com.apple.timemachine.donotpresent\n" +
            "\n" +
            "# Directories potentially created on remote AFP share\n" +
            ".AppleDB\n" +
            ".AppleDesktop\n" +
            "Network Trash Folder\n" +
            "Temporary Items\n" +
            ".apdisk\n" +
            "\n" +
            "### Sonar ###\n" +
            "#Sonar generated dir\n" +
            "/.sonar/\n" +
            "\n" +
            "### SonarQube ###\n" +
            "# SonarQube ignore files.\n" +
            "#\n" +
            "# https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner\n" +
            "# Sonar Scanner working directories\n" +
            ".sonar/\n" +
            ".scannerwork/\n" +
            "\n" +
            "# http://www.sonarlint.org/commandline/\n" +
            "# SonarLint working directories, configuration files (including credentials)\n" +
            ".sonarlint/\n" +
            "\n" +
            "### SublimeText ###\n" +
            "# Cache files for Sublime Text\n" +
            "*.tmlanguage.cache\n" +
            "*.tmPreferences.cache\n" +
            "*.stTheme.cache\n" +
            "\n" +
            "# Workspace files are user-specific\n" +
            "*.sublime-workspace\n" +
            "\n" +
            "# Project files should be checked into the repository, unless a significant\n" +
            "# proportion of contributors will probably not be using Sublime Text\n" +
            "# *.sublime-project\n" +
            "\n" +
            "# SFTP configuration file\n" +
            "sftp-config.json\n" +
            "\n" +
            "# Package control specific files\n" +
            "Package Control.last-run\n" +
            "Package Control.ca-list\n" +
            "Package Control.ca-bundle\n" +
            "Package Control.system-ca-bundle\n" +
            "Package Control.cache/\n" +
            "Package Control.ca-certs/\n" +
            "Package Control.merged-ca-bundle\n" +
            "Package Control.user-ca-bundle\n" +
            "oscrypto-ca-bundle.crt\n" +
            "bh_unicode_properties.cache\n" +
            "\n" +
            "# Sublime-github package stores a github token in this file\n" +
            "# https://packagecontrol.io/packages/sublime-github\n" +
            "GitHub.sublime-settings\n" +
            "\n" +
            "### VisualStudioCode ###\n" +
            ".vscode/*\n" +
            "!.vscode/settings.json\n" +
            "!.vscode/tasks.json\n" +
            "!.vscode/launch.json\n" +
            "!.vscode/extensions.json\n" +
            "\n" +
            "### VisualStudioCode Patch ###\n" +
            "# Ignore all local history of files\n" +
            ".history\n" +
            "\n" +
            "### Windows ###\n" +
            "# Windows thumbnail cache files\n" +
            "Thumbs.db\n" +
            "Thumbs.db:encryptable\n" +
            "ehthumbs.db\n" +
            "ehthumbs_vista.db\n" +
            "\n" +
            "# Dump file\n" +
            "*.stackdump\n" +
            "\n" +
            "# Folder config file\n" +
            "[Dd]esktop.ini\n" +
            "\n" +
            "# Recycle Bin used on file shares\n" +
            "$RECYCLE.BIN/\n" +
            "\n" +
            "# Windows Installer files\n" +
            "*.cab\n" +
            "*.msi\n" +
            "*.msix\n" +
            "*.msm\n" +
            "*.msp\n" +
            "\n" +
            "# Windows shortcuts\n" +
            "*.lnk\n" +
            "\n" +
            "### Gradle ###\n" +
            ".gradle\n" +
            "build/\n" +
            "\n" +
            "# Ignore Gradle GUI config\n" +
            "gradle-app.setting\n" +
            "\n" +
            "# Avoid ignoring Gradle wrapper jar file (.jar files are usually ignored)\n" +
            "!gradle-wrapper.jar\n" +
            "\n" +
            "# Cache of project\n" +
            ".gradletasknamecache\n" +
            "\n" +
            "# # Work around https://youtrack.jetbrains.com/issue/IDEA-116898\n" +
            "# gradle/wrapper/gradle-wrapper.properties\n" +
            "\n" +
            "### Gradle Patch ###\n" +
            "**/build/\n" +
            "\n" +
            "# End of https://www.gitignore.io/api/java,linux,macos,sonar,gradle,windows,eclipse,sonarqube,sublimetext,intellij+all,visualstudiocode";

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
        String value = "allprojects {\n" +
                "    apply plugin: 'net.saliman.cobertura'\n" +
                "    repositories {\n" +
                "       mavenCentral()\n" +
                "         maven { url \"https://repo.spring.io/snapshot\" }\n" +
                "         maven { url \"https://repo.spring.io/milestone\" }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "subprojects {\n" +
                "    apply plugin: \"java\"\n" +
                "    apply plugin: 'net.saliman.cobertura' \n" +
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
        if (!type.equals(IMPERATIVE_PROJECT)) {
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
                "   cobertura {\n" +
                "        coverageFormats = [ 'xml', 'html' ]\n" +
                "    }\n" +
                "\n" +
                "    test.finalizedBy(project.tasks.cobertura)" +
                "\n" +
                "    dependencyManagement {\n" +
                "        imports {\n" +
                "            mavenBom \"org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}\"\n" +
                "            mavenBom \"org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}\"\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "def files = subprojects.collect { new File(it.projectDir, '/build/cobertura/cobertura.ser') }\n" +
                "\n" +
                "cobertura {\n" +
                "    coverageFormats = ['xml', 'html']\n" +
                "    coverageSourceDirs = subprojects.sourceSets.main.allSource.srcDirs.flatten()\n" +
                "    coverageMergeDatafiles = files\n" +
                "}\n" +
                "\n" +
                "test.finalizedBy(project.tasks.cobertura)\n" +
                "\n" +
                "subprojects.each { project ->\n" +
                "    test.dependsOn(\":\" + project.name + \":test\")\n" +
                "}\n" +
                "\n" +
                "tasks.withType(JavaCompile) {\n" +
                "    options.compilerArgs = [\n" +
                "            '-Amapstruct.suppressGeneratorTimestamp=true'\n" +
                "    ]\n" +
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
                "    implementation project(':model')\n" +
                "    implementation project(':usecase')\n" +
                "\n";
        if (!type.equals(IMPERATIVE_PROJECT)) {
            value = value.concat("\tcompile 'org.reactivecommons.utils:object-mapper:0.1.0'\n");
        }
        value = value.concat("runtime('org.springframework.boot:spring-boot-devtools')\n" +
                "}\n" +
                "task explodedJar(type: Copy) {\n" +
                "with jar\n" +
                "into \"${buildDir}/exploded\"\n" +
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
                "server:\n" +
                "  port: 8080\n" +
                "spring:\n" +
                "  application:\n" +
                "    name: " + nameProject + "\n" +
                "  devtools:\n" +
                "    add-properties: false\n" +
                "  h2:\n" +
                "    console:\n" +
                "      enabled: true\n" +
                "      path:/h2\n" +
                "  profiles:\n" +
                "    include:\n";
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
                "\t\tsonarVersion = '"+SONAR_PLUGIN_VERSION+"'\n" +
                "\t\tnetSalimanVersion = '"+NET_SALIMAN_COBERTURA_VERSION+"'\n" +
                "\t}\n" +
                "\trepositories {\n" +
                "\t\tmavenCentral()\n" +
                "\t\tmaven { url \"https://repo.spring.io/snapshot\" }\n" +
                "\t\tmaven { url \"https://repo.spring.io/milestone\" }\n" +
                "\t}\n" +
                "\tdependencies {\n" +
                "\t\tclasspath(\"org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}\")\n" +
                "\t\tclasspath(\"org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:${sonarVersion}\")\n" +
                "\t\tclasspath(\"net.saliman:gradle-cobertura-plugin:${netSalimanVersion}\")\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "plugins {\n" +
                "\tid \"org.sonarqube\" version \""+SONAR_PLUGIN_VERSION+"\"\n" +
                "\tid \"co.com.bancolombia.cleanArchitecture\" version \"" + PluginTemplate.VERSION_PLUGIN + "\"\n" +
                "\tid \"net.saliman.cobertura\" version \""+NET_SALIMAN_COBERTURA_VERSION+"\" \n" +
                "}\n" +
                "\n" +
                "sonarqube {\n" +
                "\tproperties {\n" +
                "\t\tproperty \"sonar.sourceEnconding\", \"UTF-8\"\n" +
                "\t}\n" +
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
