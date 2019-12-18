package co.com.bancolombia;

import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.internal.project.ProjectInternal;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.gradle.api.Project;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;

import java.io.File;
import java.io.IOException;

public class UtilsTest {

    @Mock
    Project projectInternal;

    @Test
    public void getVersionPlugin() {
        Assert.assertEquals(Constants.versionPlugin, Utils.getVersionPlugin());
    }

    @Test
    public void capitalize() {
        String test1 = "capitalize";
        String test2 = "capitalizeTest";
        String test3 = "";
        String test4 = null;

        Assert.assertEquals("Capitalize", Utils.capitalize(test1));
        Assert.assertEquals("CapitalizeTest", Utils.capitalize(test2));
        Assert.assertEquals(test3, Utils.capitalize(test3));
        Assert.assertNull( Utils.capitalize(test4));
    }

    @Test
    public void decapitalize() {
        String test1 = "Decapitalize";
        String test2 = "DecapitalizeTest";
        String test3 = "DECAPITALIZE";
        String test4 = "";
        String test5 = null;
        Assert.assertEquals("decapitalize", Utils.decapitalize(test1));
        Assert.assertEquals("decapitalizeTest", Utils.decapitalize(test2));
        Assert.assertEquals("dECAPITALIZE", Utils.decapitalize(test3));
        Assert.assertEquals(test4, Utils.decapitalize(test4));
        Assert.assertNull( Utils.decapitalize(test5));
    }

    @Test
    public void readPropertiesExist() throws Exception {
        String test1 = "package";
        Assert.assertEquals("co.com.bancolombia", Utils.readProperties(test1));

    }
    @Test(expected = Exception.class)
    public void readPropertiesUnExist() throws Exception {
        String test1 = "package2";
        Utils.readProperties(test1);

    }

   /** @Test
    public void writeString() throws IOException {
        String nameFile = "temp.txt";
        File file = new File("build/functionalTest/");
        Mockito.when(projectInternal.file(Mockito.anyString())).thenReturn(file);

        Utils.writeString(projectInternal, "temp.txt", "test");
        Assert.assertEquals(1, 1);
    }**/
}
