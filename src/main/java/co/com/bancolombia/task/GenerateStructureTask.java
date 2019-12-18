package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;



public class GenerateStructureTask extends DefaultTask {
    public String _package = "co.com.bancolombia";
    public String type = "imperative";
    public String projectName = "cleanArchitecture";

    @Option(option = "package", description = "Set the principal package to use in the project")
    public void setPackage(String _package) {
        if (!_package.isEmpty()) {
            this._package = _package;
        }
    }

    @Option(option = "type", description = "Set project type, the options are  (reactive | imperative) ")
    public void setType(String type) {
        if (!type.isEmpty()) {
            this.type = type;
        }
    }

    @Option(option = "name", description = "Set the project name, by default is cleanArchitecture ")
    public void setProjectName(String projectName) {
        if (!projectName.isEmpty()) {
            this.projectName = projectName;
        }
    }


    @TaskAction
    public void GenerateStructure() throws Exception {

        System.out.println("Clean Architecture plugin version: " + Utils.getVersionPlugin());
        System.out.println("Package: " + _package);
        _package = _package.replaceAll("\\.", "\\/");
        System.out.println("Project Type: " + type);
        System.out.println("Project Name: " + projectName);
        System.out.println("Generating base directories");
        getProject().mkdir(Constants.infraestucture);
        System.out.println("Directory " + Constants.infraestucture + "has been created.");
        getProject().mkdir(Constants.domain);
        System.out.println("Directory " + Constants.domain + "has been created.");
        getProject().mkdir(Constants.application);
        System.out.println("Directory " + Constants.application + "has been created.");
        System.out.println("Directories base have been created.");

        System.out.println("Generating sub directories");
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
        getProject().file(Constants.gradleProperties).createNewFile();


        System.out.println("Generated Base Files");
        System.out.println("Writing in Files");

        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle), Constants.buildGradleUseCaseContent);
        Utils.writeString(getProject(), Constants.lombokConfig, Constants.lombokConfigContent);
        Utils.writeString(getProject(), Constants.gitignore, Constants.gitIgnoreContent);
        Utils.writeString(getProject(), Constants.readMe, Constants.readmeContent);
        Utils.writeString(getProject(), Constants.gradleProperties, Constants.getGradlePropertiesContent(_package));
        Utils.writeString(getProject(), Constants.settingsGradle, Constants.getSettingsGradleContent(this.projectName));
        Utils.writeString(getProject(), Constants.mainGradle, Constants.mainGradleContent);
        Utils.writeString(getProject(), Constants.buildGradle, Constants.getBuildGradleContent());
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.buildGradle), Constants.buildGradleApplicationContent);
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.applicationProperties), Constants.getApplicationPropertiesContent(this.projectName));
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.log4j), Constants.log4jContent);
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.mainApplication), Constants.getMainApplicationContent(this.projectName));

        System.out.println("Writed in Files");

    }


}
