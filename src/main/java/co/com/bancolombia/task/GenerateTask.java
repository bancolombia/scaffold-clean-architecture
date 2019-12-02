package co.com.bancolombia.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GenerateTask extends DefaultTask {
    public static  String packages ;
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

    private final ClassLoader loader = getClass().getClassLoader();

public void GenerateTask(){
    System.out.println("constructor");
    this.setGroup("Clean Architecture");
    this.setDescription("Generar Scaffold");

}

    @Option(option = "package",description = "set the package")
    public void setPackage(String pac) {
        this.packages =pac;
    }
    @TaskAction
    public void Generate() throws IOException {
   if (packages.isEmpty()){
       System.out.println("Set the package " );
       return;
   }
        System.out.println("packages " + packages);
        System.out.println("Generating Base Dirs");
        getProject().mkdir(infraestucture);
        System.out.println("create dir: " + infraestucture);
        getProject().mkdir(domain);
        System.out.println("create dir: " + domain);
        getProject().mkdir(application);
        System.out.println("create dir:" + application);
        System.out.println("Generated Base Dirs");

        System.out.println("Generating Childs Dirs");
        getProject().mkdir(application.concat("/").concat(mainJava).concat("/").concat(packages).concat("/").concat(config));
        getProject().mkdir(application.concat("/").concat(mainResource));

        getProject().mkdir(infraestucture.concat("/").concat(drivenAdapters));
        getProject().mkdir(infraestucture.concat("/").concat(entryPoints));
        getProject().mkdir(infraestucture.concat("/").concat(helpers));

        getProject().mkdir(domain.concat("/").concat(model).concat("/").concat(mainJava).concat("/").concat(packages).concat("/").concat(model));
        getProject().mkdir(domain.concat("/").concat(usecase).concat("/").concat(mainJava).concat("/").concat(packages).concat("/").concat(usecase));

        System.out.println("Generated Childs Dirs");

        System.out.println("Generating Base Files");
        getProject().file(domain.concat("/").concat(model).concat("/").concat(buildGradle)).createNewFile();
        getProject().file(domain.concat("/").concat(usecase).concat("/").concat(buildGradle)).createNewFile();

        getProject().file(application.concat("/").concat(buildGradle)).createNewFile();
        getProject().file(application.concat("/").concat(mainResource).concat(applicationProperties)).createNewFile();
        getProject().file(application.concat("/").concat(mainResource).concat(log4j)).createNewFile();
        getProject().file(application.concat("/").concat(mainJava).concat(app).concat(mainApplication)).createNewFile();

        getProject().file(mainGradle).createNewFile();
        getProject().file(lombokConfig).createNewFile();
        File f = new File(domain.concat("/").concat(model).concat("/").concat(buildGradle));
        FileWriter fw = new FileWriter(f);
        fw.write(buildGradleModel);
        fw.close();
      // Files.write( Paths.get(domain.concat("/").concat(model).concat("/").concat(buildGradle)), buildGradleModel.getBytes());
      //  Files.write( Paths.get(domain.concat("/").concat(usecase).concat("/").concat(buildGradle)), buildGradleUseCase.getBytes());


        System.out.println("Generated Base Files");

    }

}
