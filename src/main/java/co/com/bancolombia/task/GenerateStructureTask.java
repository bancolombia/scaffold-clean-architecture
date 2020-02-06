package co.com.bancolombia.task;

import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
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


public class GenerateStructureTask extends DefaultTask {
    private Logger logger = getProject().getLogger();
    private String packageName = "co.com.bancolombia";
    private String type = "imperative";
    private String projectName = "cleanArchitecture";

    @Option(option = "package", description = "Set the principal package to use in the project")
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    @Option(option = "type", description = "Set project type, the options are  (reactive | imperative) ")
    public void setType(String type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set the project name, by default is cleanArchitecture ")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @TaskAction
    public void generateStructureTask() throws IOException {
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.lifecycle("Project Type: {}", type);
        logger.lifecycle("Project Name: {}", projectName);
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

        dirs.add(ScaffoldTemplate.DEPLOYMENT);

        dirs.add(Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.CONFIG));
        dirs.add(Constants.APPLICATION.concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName));
        dirs.add(Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES));

        dirs.add(Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS));
        dirs.add(Constants.INFRASTRUCTURE.concat("/").concat(Constants.ENTRY_POINTS));
        dirs.add(Constants.INFRASTRUCTURE.concat("/").concat(Constants.HELPERS));

        dirs.add(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL));
        dirs.add(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL));
        dirs.add(Constants.DOMAIN.concat("/").concat(Constants.USECASE_FOLDER).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.USECASE_FOLDER));
        dirs.add(Constants.DOMAIN.concat("/").concat(Constants.USECASE_FOLDER).concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.USECASE_FOLDER));

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
                .path(Constants.DOMAIN.concat("/").concat(Constants.USECASE_FOLDER).concat("/")
                        .concat(Constants.BUILD_GRADLE))
                .content(UseCaseTemplate.BUILD_GRADLE_USE_CASE_CONTENT)
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/")
                        .concat(Constants.BUILD_GRADLE))
                .content("")
                .build());

        files.add(FileModel
                .builder()
                .path(ScaffoldTemplate.DEPLOYMENT.concat("/").concat(ScaffoldTemplate.DOCKERFILE))
                .content(ScaffoldTemplate.DOCKER_FILE_CONTENT)
                .build());

        files.add(FileModel
                .builder()
                .path(ScaffoldTemplate.LOMBOK_CONFIG)
                .content(ScaffoldTemplate.LOMBOK_CONFIG_CONTENT)
                .build());

        files.add(FileModel
                .builder()
                .path(ScaffoldTemplate.GITIGNORE)
                .content(ScaffoldTemplate.GIT_IGNORE_CONTENT)
                .build());

        files.add(FileModel
                .builder()
                .path(ScaffoldTemplate.READ_ME)
                .content(ScaffoldTemplate.README_CONTENT)
                .build());

        files.add(FileModel
                .builder()
                .path(ScaffoldTemplate.GRADLE_PROPERTIES)
                .content(ScaffoldTemplate.getGradlePropertiesContent(packageName))
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.SETTINGS_GRADLE)
                .content(ScaffoldTemplate.getSettingsGradleContent(this.projectName))
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.BUILD_GRADLE)
                .content(ScaffoldTemplate.getBuildGradleContent())
                .build());

        final String MAIN_RESOURCES = Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES)
                .concat("/");

        files.add(FileModel
                .builder()
                .path(MAIN_RESOURCES.concat(ScaffoldTemplate.APPLICATION_PROPERTIES))
                .content(ScaffoldTemplate.getApplicationPropertiesContent(this.projectName))
                .build());

        files.add(FileModel
                .builder()
                .path(MAIN_RESOURCES.concat(ScaffoldTemplate.LOG_4_J))
                .content(ScaffoldTemplate.LOG_4_J_CONTENT)
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.MAIN_GRADLE)
                .content(ScaffoldTemplate.getMainGradleContent(type))
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.APPLICATION.concat("/").concat(Constants.BUILD_GRADLE))
                .content(ScaffoldTemplate.getBuildGradleApplicationContent(type))
                .build());

        files.add(FileModel
                .builder()
                .path(Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/")
                        .concat(packageName).concat("/").concat(ScaffoldTemplate.MAIN_APPLICATION))
                .content(ScaffoldTemplate.getMainApplicationContent(this.packageName))
                .build());
        
        return files;

    }
}
