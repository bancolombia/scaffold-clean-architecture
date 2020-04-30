package co.com.bancolombia.models.entrypoints;

import co.com.bancolombia.models.adapters.entrypoints.ApiRest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ApiRestTest {

    ApiRest apiRest;

    @Before
    public void init() throws IOException {
        apiRest = new ApiRest();
    }

    @Test
    public void getAppServiceImportsTest() {
        Assert.assertEquals("", apiRest.getAppServiceImports());
    }

    @Test
    public void getPropertiesFileContentTest() {
        Assert.assertNull(apiRest.getPropertiesFileContent());
    }
}
