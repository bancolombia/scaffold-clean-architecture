package co.com.bancolombia;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConstantsTest {

    @Test
    public void getSettingsGradleContent() {
        String nameProject = "testProjectName";
        Assert.assertTrue(Constants.getSettingsGradleContent(nameProject).contains(nameProject));
    }

    @Test(expected = NullPointerException.class)
    public void getSettingsGradleContentException() {
        String nameProject = null;
        Assert.assertTrue(Constants.getSettingsGradleContent(nameProject).contains(nameProject));
    }

    @Test
    public void getApplicationPropertiesContent() {
        String nameProject = "testProjectName";
        Assert.assertTrue(Constants.getApplicationPropertiesContent(nameProject).contains(nameProject));
    }

    @Test
    public void getMainApplicationContent() {
        String nameProject = "testProjectName";
        Assert.assertTrue(Constants.getMainApplicationContent(nameProject).contains(nameProject));
    }

    @Test
    public void getModel() {
        String modelName = "testProjectName";
        String _package = "demo.package";
        Assert.assertTrue(Constants.getModel(modelName, _package).contains(modelName));
        Assert.assertTrue(Constants.getModel(modelName, _package).contains(_package));
    }

    @Test
    public void getBuildGradleContent() {
        Assert.assertEquals(String.class, Constants.getBuildGradleContent().getClass());
    }

    @Test
    public void getInterfaceModel() {
        String modelName = "testProjectName";
        String _package = "demo.package";
        Assert.assertTrue(Constants.getInterfaceModel(modelName, _package).contains(modelName));
        Assert.assertTrue(Constants.getInterfaceModel(modelName, _package).contains(_package));
    }

    @Test
    public void getGradlePropertiesContent() {
        String _package = "demo.package";
        Assert.assertTrue(Constants.getGradlePropertiesContent(_package).contains(_package));
    }

    @Test
    public void getbuildGradleApplicationContent() {
        Assert.assertEquals(String.class, Constants.getbuildGradleApplicationContent().getClass());
    }

    @Test
    public void getUseCase() {
        String useCaseName = "testProjectName";
        String _package = "demo.package";
        Assert.assertTrue(Constants.getUseCase(useCaseName, _package).contains(useCaseName));
        Assert.assertTrue(Constants.getUseCase(useCaseName, _package).contains(_package));
    }
}
