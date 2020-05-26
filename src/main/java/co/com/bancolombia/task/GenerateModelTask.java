package co.com.bancolombia.task;

import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.templates.ModelTemplate;
import co.com.bancolombia.templates.PluginTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateModelTask extends DefaultTask {
    private String modelName = "";
    private String modelSubPackage = "";
    private String packageName;
    private Logger logger = getProject().getLogger();

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) {
        this.modelName = modelName;
    }

    @Option(option = "package", description = "Set the model sub package")
    public void setPackageProject(String modelSubPackage) {
        this.modelSubPackage = modelSubPackage;
    }

    @TaskAction
    public void generateModelTask() throws IOException {
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usege: gradle generateModel --name modelName");
        }

        packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        logger.lifecycle("Project  Sub Package: {}", modelSubPackage);
        packageName = packageName.replaceAll("\\.", "\\/");
        modelSubPackage = modelSubPackage.replaceAll("\\.", "\\/");
        logger.lifecycle("Model Name: {}", modelName);
        logger.lifecycle(PluginTemplate.GENERATING_CHILDS_DIRS);
        createDirs();
        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        logger.lifecycle(PluginTemplate.GENERATING_FILES);
        writeFiles();
        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);

    }

    private void createDirs() {
        List<String> dirs = getDirsToCreate();
        dirs.forEach(getProject()::mkdir);
    }

    private List<String> getDirsToCreate() {
        List<String> dirs = new ArrayList<>();

        dirs.add(Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                .concat("/").concat(Constants.MAIN_JAVA).concat("/")
                .concat(packageName).concat("/").concat(Constants.MODEL)
                .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage).concat("/")
                .concat(Constants.GATEWAYS));

        dirs.add(Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                .concat("/").concat(Constants.TEST_JAVA).concat("/")
                .concat(packageName).concat("/").concat(Constants.MODEL)
                .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage));

        return dirs;

    }

    private void writeFiles() throws IOException {
        List<FileModel> files = getFilesToCreate();
        for (FileModel file : files) {
            Utils.writeString(getProject(), file.getPath(), file.getContent());
        }
    }

    private List<FileModel> getFilesToCreate() {
        List<FileModel> files = new ArrayList<>();

        files.add(FileModel
                .builder()
                .path(Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                        .concat("/").concat(Constants.MAIN_JAVA).concat("/")
                        .concat(packageName).concat("/").concat(Constants.MODEL)
                        .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage).concat("/")
                        .concat(Constants.GATEWAYS).concat("/")
                        .concat(Utils.capitalize(modelName) + Constants.REPOSITORY
                                + Constants.JAVA_EXTENSION))
                .content(ModelTemplate.getInterfaceModel(modelName, packageName))
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                        .concat("/").concat(Constants.MAIN_JAVA).concat("/")
                        .concat(packageName).concat("/").concat(Constants.MODEL)
                        .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage)
                        .concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_EXTENSION))
                .content(ModelTemplate.getModel(modelName, packageName))
                .build());


        return files;
    }
}
