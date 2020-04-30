package co.com.bancolombia.models.entrypoints;

import co.com.bancolombia.models.adapters.entrypoints.ApiReactive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ApiReactiveTest {

    ApiReactive apiReactive;

    @Before
    public void init() throws IOException {
        apiReactive = new ApiReactive();
    }

    @Test
    public void getAppServiceImportsTest() {
        Assert.assertEquals("",apiReactive.getAppServiceImports());
    }

    @Test
    public void getPropertiesFileContentTest() {
        Assert.assertNull(apiReactive.getPropertiesFileContent());
    }
}
