package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.models.adapters.drivenadapters.AsyncEventBusDrivenAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AsyncEventBusDrivenAdapterTest {

    AsyncEventBusDrivenAdapter adapter;

    @Before
    public void init() throws IOException {
        adapter = new AsyncEventBusDrivenAdapter();
    }

    @Test
    public void getInterfaceModuleTest() {
        Assert.assertNotNull(adapter.getInterfaceModule());
    }

    @Test
    public void getPropertiesFileContentTest() {
        Assert.assertNull(adapter.getPropertiesFileContent());
    }


}
