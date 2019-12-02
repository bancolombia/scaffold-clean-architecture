package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class GenerateTask extends DefaultTask {
    public String _package = "";
    public String type = "";
    File f;
    FileWriter fw;




    @Option(option = "package", description = "Set the package")
    public void setPackage(String _package) {
        this._package = _package;
    }

    @Option(option = "type", description = "Set the type of the project (reactive | imperative) ")
    public void setType(String type) {
        this.type = type;
    }


    @TaskAction
    public void Generate() throws IOException {
        if (_package.isEmpty()) {
            System.out.println("Set the package ");
            return;
        }

        if (type.isEmpty()) {
            System.out.println("Set the type ");
            return;
        }
        System.out.println("packages " + _package);
        System.out.println("Generating Base Dirs");
        getProject().mkdir(Constants.infraestucture);
        System.out.println("create dir: " + Constants.infraestucture);
        getProject().mkdir(Constants.domain);
        System.out.println("create dir: " + Constants.domain);
        getProject().mkdir(Constants.application);
        System.out.println("create dir:" + Constants.application);
        System.out.println("Generated Base Dirs");

        System.out.println("Generating Childs Dirs");
        getProject().mkdir(Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.config));
        getProject().mkdir(Constants.application.concat("/").concat(Constants.mainResource));

        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.drivenAdapters));
        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.entryPoints));
        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.helpers));

        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.usecase));

        System.out.println("Generated Childs Dirs");

        System.out.println("Generating Base Files");
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.buildGradle)).createNewFile();
        getProject().file(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle)).createNewFile();

        getProject().file(Constants.application.concat("/").concat(Constants.buildGradle)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainResource).concat(Constants.applicationProperties)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainResource).concat(Constants.log4j)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainJava).concat(Constants.app).concat(Constants.mainApplication)).createNewFile();

        getProject().file(Constants.mainGradle).createNewFile();
        getProject().file(Constants.lombokConfig).createNewFile();
        getProject().file(Constants.settingsGradle).createNewFile();
        getProject().file(Constants.gitignore).createNewFile();
        getProject().file(Constants.readMe).createNewFile();

        System.out.println("Generated Base Files");
        System.out.println("Writing in Files");

         f = new File(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle));
         fw = new FileWriter(f);
        fw.write(Constants.buildGradleUseCaseContent);

        f = new File(Constants.lombokConfig);
        fw = new FileWriter(f);
        fw.write(Constants.lombokConfigContent);

        f = new File(Constants.gitignore);
        fw = new FileWriter(f);
        fw.write(Constants.gitIgnoreContent);

        f = new File(Constants.readMe);
        fw = new FileWriter(f);
        fw.write(Constants.readmeContent);

        fw.close();

        System.out.println("Writed in Files");



    }

}
