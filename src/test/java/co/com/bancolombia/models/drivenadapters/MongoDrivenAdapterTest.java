package co.com.bancolombia.models.drivenadapters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MongoDrivenAdapterTest {

    MongoDrivenAdapter adapter;

    @Before
    public void init() throws IOException {
        adapter = new MongoDrivenAdapter();
    }

    @Test
    public void getPropertiesFileContentTest() {
        Assert.assertNull(adapter.getPropertiesFileContent());
    }
}
