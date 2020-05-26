package co.com.bancolombia;

import co.com.bancolombia.templates.*;
import org.junit.Assert;
import org.junit.Test;

public class ConstantsTest {

    @Test
    public void getSettingsGradleContent() {
        String nameProject = "testProjectName";

        String act = ScaffoldTemplate.getSettingsGradleContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);

    }

    @Test(expected = NullPointerException.class)
    public void getSettingsGradleContentException() {
        String nameProject = null;

        String act = ScaffoldTemplate.getSettingsGradleContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getApplicationPropertiesContent() {
        String nameProject = "testProjectName";

        String act = ScaffoldTemplate.getApplicationPropertiesContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getMainApplicationContent() {
        String nameProject = "testProjectName";

        String act = ScaffoldTemplate.getMainApplicationContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getModel() {
        String modelName = "testProjectName";
        String packageName = "demo.package";

        String act = ModelTemplate.getModel(modelName, packageName, "");

        Assert.assertTrue(act.contains(modelName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getBuildGradleContent() {
        String act =  ScaffoldTemplate.getBuildGradleContent();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getInterfaceModel() {
        String modelName = "testProjectName";
        String packageName = "demo.package";

        String act =  ModelTemplate.getInterfaceModel(modelName, packageName, "");

        Assert.assertTrue(act.contains(modelName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getGradlePropertiesContent() {
        String packageName = "demo.package";

        String act =  ScaffoldTemplate.getGradlePropertiesContent(packageName);

        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getUseCase() {
        String useCaseName = "testProjectName";
        String packageName = "demo.package";

        String act =  UseCaseTemplate.getUseCase(useCaseName, packageName);

        Assert.assertTrue(act.contains(useCaseName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getApiRestClassContent(){
        String packageName = "demo.package";

        String act =  EntryPointTemplate.getApiRestClassContent(packageName);

        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);

    }
    @Test
    public void getBuildGradleHelperMongoRepository(){
        String act =  HelperTemplate.getBuildGradleHelperMongoRepository();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleHelperJPARepository(){
        String act =  HelperTemplate.getBuildGradleHelperJPARepository();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleMongoRepository(){
        String act =  DrivenAdapterTemplate.getBuildGradleMongoRepository();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleApiRest(){
        String act =  EntryPointTemplate.getBuildGradleApiRest();

        Assert.assertTrue(act instanceof  String);
    }

}
