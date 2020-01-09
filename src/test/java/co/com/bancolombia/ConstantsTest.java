package co.com.bancolombia;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConstantsTest {

    @Test
    public void getSettingsGradleContent() {
        String nameProject = "testProjectName";

        String act = Constants.getSettingsGradleContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);

    }

    @Test(expected = NullPointerException.class)
    public void getSettingsGradleContentException() {
        String nameProject = null;

        String act = Constants.getSettingsGradleContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getApplicationPropertiesContent() {
        String nameProject = "testProjectName";

        String act = Constants.getApplicationPropertiesContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getMainApplicationContent() {
        String nameProject = "testProjectName";

        String act = Constants.getMainApplicationContent(nameProject);

        Assert.assertTrue(act.contains(nameProject));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getModel() {
        String modelName = "testProjectName";
        String packageName = "demo.package";

        String act = Constants.getModel(modelName, packageName);

        Assert.assertTrue(act.contains(modelName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getBuildGradleContent() {
        String act =  Constants.getBuildGradleContent();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getInterfaceModel() {
        String modelName = "testProjectName";
        String packageName = "demo.package";

        String act =  Constants.getInterfaceModel(modelName, packageName);

        Assert.assertTrue(act.contains(modelName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getGradlePropertiesContent() {
        String packageName = "demo.package";

        String act =  Constants.getGradlePropertiesContent(packageName);

        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getUseCase() {
        String useCaseName = "testProjectName";
        String packageName = "demo.package";

        String act =  Constants.getUseCase(useCaseName, packageName);

        Assert.assertTrue(act.contains(useCaseName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);

    }

    @Test
    public void getApiRestClassContent(){
        String packageName = "demo.package";

        String act =  Constants.getApiRestClassContent(packageName);

        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof  String);

    }
    @Test
    public void getBuildGradleHelperMongoRepository(){
        String act =  Constants.getBuildGradleHelperMongoRepository();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleHelperJPARepository(){
        String act =  Constants.getBuildGradleHelperJPARepository();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleMongoRepository(){
        String act =  Constants.getBuildGradleMongoRepository();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleSecretsManager(){
        String act =  Constants.getBuildGradleSecretsManager();

        Assert.assertTrue(act instanceof  String);
    }

    @Test
    public void getBuildGradleApiRest(){
        String act =  Constants.getBuildGradleApiRest();

        Assert.assertTrue(act instanceof  String);
    }

}
