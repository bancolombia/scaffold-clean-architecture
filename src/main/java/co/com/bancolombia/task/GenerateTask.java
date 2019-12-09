package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import kotlin.reflect.jvm.internal.impl.load.java.Constant;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GenerateTask extends DefaultTask {
    public String _package = "";
    public String type = "imperative";
    public String nameProject = "cleanArchitecture";
    File f;
    FileWriter fw;


    @Option(option = "package", description = "Set the package")
    public void setPackage(String _package) {
        this._package = _package;
    }

    @Option(option = "type", description = "Set the type of the project (reactive | imperative) ")
    public void setType(String type) {
        if (!type.isEmpty()){
            this.type = type;
        }
    }

    @Option(option = "name", description = "Set the name of the projectm default is cleanArchitecture ")
    public void setNameProject(String nameProject) {
        if (!nameProject.isEmpty()){
            this.nameProject = nameProject;
        }
    }


    @TaskAction
    public void Generate() throws IOException {
        if (_package.isEmpty()) {
            System.out.println("Set the package ");
            System.exit(-1);
            return;
        }
        _package=  _package.replaceAll("\\.","/");
        System.out.println("Package: " + _package);
        System.out.println("Type Project: " + type);
        System.out.println("Name Project: " + nameProject);
        System.out.println("Generating Base Dirs");
        getProject().mkdir(Constants.infraestucture);
        System.out.println("Created dir : " + Constants.infraestucture);
        getProject().mkdir(Constants.domain);
        System.out.println("Created dir: " + Constants.domain);
        getProject().mkdir(Constants.application);
        System.out.println("Created dir: " + Constants.application);
        System.out.println("Created dir: " + Constants.application);
        System.out.println("Generated Base Dirs");

        System.out.println("Generating Childs Dirs");
        getProject().mkdir(Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.config));
        getProject().mkdir(Constants.application.concat("/").concat(Constants.testJava).concat("/").concat(_package));
        getProject().mkdir(Constants.application.concat("/").concat(Constants.mainResource));

        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.drivenAdapters));
        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.entryPoints));
        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.helpers));

        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.testJava).concat("/").concat(_package).concat("/").concat(Constants.model));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.usecase));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.testJava).concat("/").concat(_package).concat("/").concat(Constants.usecase));

        System.out.println("Generated Childs Dirs");

        System.out.println("Generating Base Files");
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.buildGradle)).createNewFile();
        getProject().file(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle)).createNewFile();

        getProject().file(Constants.application.concat("/").concat(Constants.buildGradle)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.applicationProperties)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.log4j)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.mainApplication)).createNewFile();

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
        fw.close();

        f = new File(Constants.lombokConfig);
        fw = new FileWriter(f);
        fw.write(Constants.lombokConfigContent);
        fw.close();

        f = new File(Constants.gitignore);
        fw = new FileWriter(f);
        fw.write(Constants.gitIgnoreContent);
        fw.close();

        f = new File(Constants.readMe);
        fw = new FileWriter(f);
        fw.write(Constants.readmeContent);
        fw.close();

        f = new File(Constants.settingsGradle);
        fw = new FileWriter(f);
        fw.write(Constants.getSettingsGradleContent(this.nameProject));
        fw.close();

        f = new File(Constants.mainGradle);
        fw = new FileWriter(f);
        fw.write(Constants.mainGradleContent);
        fw.close();

        f = new File(Constants.buildGradle);
        fw = new FileWriter(f);
        fw.write(Constants.buildGradleContent);
        fw.close();

        f = new File(Constants.application.concat("/").concat(Constants.buildGradle));
        fw = new FileWriter(f);
        fw.write(Constants.buildGradleApplicationContent);
        fw.close();

        f = new File(Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.applicationProperties));
        fw = new FileWriter(f);
        fw.write(Constants.getApplicationPropertiesContent(this.nameProject));
        fw.close();

        f = new File(Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.log4j));
        fw = new FileWriter(f);
        fw.write(Constants.log4jContent);
        fw.close();

        f = new File(Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.mainApplication));
        fw = new FileWriter(f);
        fw.write(Constants.getMainApplicationContent(this.nameProject));
        fw.close();


        System.out.println("Writed in Files");


    }

}
