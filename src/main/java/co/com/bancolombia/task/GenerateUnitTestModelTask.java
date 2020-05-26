package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.models.AttributeClassModel;
import co.com.bancolombia.models.FileModel;
import co.com.bancolombia.models.StructureClassModel;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import co.com.bancolombia.templates.UnitTestModelTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GenerateUnitTestModelTask extends DefaultTask {

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
    public void generateUnitTestModelTask() throws IOException {
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usege: gradle generateModel --name modelName");
        }

        packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        modelSubPackage = modelSubPackage.replaceAll("\\.", "\\/");
        logger.lifecycle("Model Name: {}", modelName);
        writeFiles();
        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);

    }

    private FileModel getClassOriginal(Project project, String nameFile) throws IOException {

        Stream<String> textFile = Utils.readFile(project,nameFile);

        StructureClassModel classModel = new StructureClassModel();
        List<String> imports = new LinkedList<>();
        List<String> tags = new LinkedList<>();
        List<AttributeClassModel> attributes = new LinkedList<>();


        textFile.parallel().forEach( line -> {
            line = line.trim().replace(";", "");

            if(line.startsWith("package"))
                classModel.setPackpage(line);
            else if(line.startsWith("import")) {
                imports.add(line);
            }else if(line.startsWith("@")) {
                tags.add(line);
                if(line.contains("Builder")){
                    classModel.setBuilder(true);
                }
            }else if(line.startsWith("class") || line.startsWith("public class")) {
                String class_name = line.replace("public", "").replace("class", "").replace("{", "").trim();

                if(class_name.contains("extends")){
                    class_name = class_name.split("extends")[0];
                }

                if(class_name.contains("implements")){
                    class_name = class_name.split("implements")[0];
                }

                classModel.setName_class(class_name.trim());
                classModel.setName_test_class(class_name.trim()+"Test");
            }else if(line.startsWith("public") || line.startsWith("private") || line.startsWith("protected")){
                String attribute = line.replace("public", "")
                        .replace("private", "")
                        .replace("protected", "")
                        .replace("final", "");
                String[] vector = attribute.trim().split(" ");
                attributes.add(new AttributeClassModel(vector[1], vector[0]));
            }
        });

        classModel.setAttributes(attributes);
        classModel.setTags(tags);
        classModel.setImports(imports);

        logger.lifecycle("content {}", Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                .concat("/").concat(Constants.TEST_JAVA).concat("/")
                .concat(packageName).concat("/").concat(Constants.MODEL)
                .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage)
                .concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_UNITTEST_EXTENSION));

        FileModel unitTest = FileModel
                .builder()
                .path(Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                        .concat("/").concat(Constants.TEST_JAVA).concat("/")
                        .concat(packageName).concat("/").concat(Constants.MODEL)
                        .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage)
                        .concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_UNITTEST_EXTENSION))
                .content(UnitTestModelTemplate.getUnitTestModel(classModel))
                .build();

        return unitTest;
    }

    private void writeFiles() throws IOException {
        logger.lifecycle("path {}", "entro");
        String pathClass = Constants.DOMAIN.concat("/").concat(Constants.MODEL)
                .concat("/").concat(Constants.MAIN_JAVA).concat("/")
                .concat(packageName).concat("/").concat(Constants.MODEL)
                .concat("/").concat((modelSubPackage.isEmpty())?Utils.decapitalize(modelName):modelSubPackage)
                .concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_EXTENSION);
        logger.lifecycle("path {}", pathClass);
        FileModel file = getClassOriginal(getProject(), pathClass);

        Utils.writeString(getProject(), file.getPath(), file.getContent());

    }
}
