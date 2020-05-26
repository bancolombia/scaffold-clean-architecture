package co.com.bancolombia;

import co.com.bancolombia.templates.PluginTemplate;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;

public class UtilsTest {


    @Test
    public void getVersionPlugin() {
        Assert.assertEquals(PluginTemplate.VERSION_PLUGIN, Utils.getVersionPlugin());
    }

    @Test
    public void capitalize() {
        String test1 = "capitalize";
        String test2 = "capitalizeTest";

        Assert.assertEquals("Capitalize", Utils.capitalize(test1));
        Assert.assertEquals("CapitalizeTest", Utils.capitalize(test2));
    }

    @Test
    public void decapitalize() {
        String test1 = "Decapitalize";
        String test2 = "DecapitalizeTest";
        String test3 = "DECAPITALIZE";
        Assert.assertEquals("decapitalize", Utils.decapitalize(test1));
        Assert.assertEquals("decapitalizeTest", Utils.decapitalize(test2));
        Assert.assertEquals("dECAPITALIZE", Utils.decapitalize(test3));
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

    @Test
    public void tryParse() {
        String test = "1";

        int act = Utils.tryParse(test);

        Assert.assertEquals(1, act);
    }

    @Test(expected = NumberFormatException.class)
    public void tryParseUnParse() {
        String test = "test";
        Utils.tryParse(test);

    }

    @Test
    public void readFile() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/resources")).build();
        String response = Utils.readFile(project, "temp.txt").collect(Collectors.joining());

        Assert.assertTrue(response instanceof String);
        Assert.assertEquals("hello", response);
    }

    @Test
    public void writeString() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/tmp")).build();
        Utils.writeString(project, "temp.txt", "hello");
        String response = Utils.readFile(project, "temp.txt").collect(Collectors.joining());

        Assert.assertTrue(response instanceof String);
        Assert.assertEquals("hello", response);
    }

    @Test
    public void finderSubProjects() {
        List<File> files = Utils.finderSubProjects("src/test/resources");

        Assert.assertEquals(0, files.size());

        List<File> files2 = Utils.finderSubProjects("src/test/resources/finderSubProjects/");

        Assert.assertEquals(2, files2.size());
    }

    @Test
    public void getValuesByClass(){
        Assert.assertEquals(Utils.getValuesByClass("String"), "\"String\"");
        Assert.assertEquals(Utils.getValuesByClass("int"), 1234);
        Assert.assertEquals(Utils.getValuesByClass("short"), 1234);
        Assert.assertEquals(Utils.getValuesByClass("char"), "\'c\'");
        Assert.assertEquals(Utils.getValuesByClass("double"), 12345.0d);
        Assert.assertEquals(Utils.getValuesByClass("Date"), "new Date()");
        Assert.assertEquals(Utils.getValuesByClass("boolean"), false);
        Assert.assertEquals(Utils.getValuesByClass("float"), 0.0f);
        Assert.assertEquals(Utils.getValuesByClass("long"), 0L);
        Assert.assertEquals(Utils.getValuesByClass("byte"), 0);
        Assert.assertEquals(Utils.getValuesByClass("List<T>"), "new LinkedList<>()");
        Assert.assertNull(Utils.getValuesByClass("Object"));
    }

    @Test
    public void getDelta(){
        Assert.assertEquals(Utils.getDelta("String"), "");
        Assert.assertEquals(Utils.getDelta("double"), ", 0");
        Assert.assertEquals(Utils.getDelta("float"), ", 0");
    }

}
