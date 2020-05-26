package co.com.bancolombia.models;
 
import org.junit.Assert;
import org.junit.Test;

public class AttributeClassModelTest{

    @Test
    public void attributeClassModelAllArgsTest(){
        AttributeClassModel attributeClassModel = new AttributeClassModel(
                "String",
                "String");

        attributeClassModel.setName("String");
        attributeClassModel.setAClass("String");

        Assert.assertEquals("String", attributeClassModel.getName());
        Assert.assertEquals("String", attributeClassModel.getAClass());
    }

}
