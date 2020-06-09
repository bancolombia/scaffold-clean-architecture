package co.com.bancolombia;

import co.com.bancolombia.templates.DrivenAdapterTemplate;
import co.com.bancolombia.templates.HelperTemplate;
import co.com.bancolombia.templates.ModelTemplate;
import org.junit.Assert;
import org.junit.Test;

public class ConstantsTest {


    @Test
    public void getModel() {
        String modelName = "testProjectName";
        String packageName = "demo.package";

        String act = ModelTemplate.getModel(modelName, packageName);

        Assert.assertTrue(act.contains(modelName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof String);

    }


    @Test
    public void getInterfaceModel() {
        String modelName = "testProjectName";
        String packageName = "demo.package";

        String act = ModelTemplate.getInterfaceModel(modelName, packageName);

        Assert.assertTrue(act.contains(modelName));
        Assert.assertTrue(act.contains(packageName));
        Assert.assertTrue(act instanceof String);
    }

    @Test
    public void getBuildGradleHelperMongoRepository() {
        String act = HelperTemplate.getBuildGradleHelperMongoRepository();

        Assert.assertTrue(act instanceof String);
    }

    @Test
    public void getBuildGradleHelperJPARepository() {
        String act = HelperTemplate.getBuildGradleHelperJPARepository();

        Assert.assertTrue(act instanceof String);
    }

    @Test
    public void getBuildGradleMongoRepository() {
        String act = DrivenAdapterTemplate.getBuildGradleMongoRepository();

        Assert.assertTrue(act instanceof String);
    }

}
