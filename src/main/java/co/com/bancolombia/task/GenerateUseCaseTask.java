package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import co.com.bancolombia.templates.ScaffoldTemplate;
import co.com.bancolombia.templates.UseCaseTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateUseCaseTask extends DefaultTask {
    private Logger logger = getProject().getLogger();
    private String useCaseName = "";
    private String packageName;
    private String useCaseDir;


    @Option(option = "name", description = "Set the UseCase name")
    public void setNameProject(String useCaseName) {
        this.useCaseName = useCaseName;
    }

    @TaskAction
    public void generateUseCaseTask() throws IOException {
        throwUseCase();
        packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");

        logger.lifecycle("Use Case Name: {}", useCaseName);

        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        createDirs();
        logger.lifecycle(PluginTemplate.GENERATING_CHILDS_DIRS);

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

        List<String> pathUseCase = new ArrayList<>();

        pathUseCase.add(Constants.DOMAIN);
        pathUseCase.add(Constants.USECASE_FOLDER);
        pathUseCase.add(Constants.MAIN_JAVA);
        pathUseCase.add(packageName);
        pathUseCase.add(Constants.USECASE_FOLDER);
        pathUseCase.add(Utils.decapitalize(useCaseName));
        useCaseDir = Utils.concatSeparator( pathUseCase);
        dirs.add(useCaseDir);

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
                .path(useCaseDir.concat("/")
                        .concat(Utils.capitalize(useCaseName)
                                + Constants.USECASE_CLASS_NAME
                                + Constants.JAVA_EXTENSION))
                .content(UseCaseTemplate.getUseCase(useCaseName, packageName))
                .build());
        return files;
    }

    private void throwUseCase() {
        if (useCaseName.isEmpty()) {
            throw new IllegalArgumentException("No use case name, usege: gradle generateUseCase --name useCaseName");
        }
    }
}

