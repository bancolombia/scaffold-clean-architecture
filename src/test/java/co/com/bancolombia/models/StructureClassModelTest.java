package co.com.bancolombia.models;
 
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class StructureClassModelTest{

 @Test
 public void structureClassModelNoArgsTest(){
     StructureClassModel structureClassModel = new StructureClassModel();
     structureClassModel.setPackpage("String");
     structureClassModel.setImports(new LinkedList<>());
     structureClassModel.setTags(new LinkedList<>());
     structureClassModel.setName_class("String");
     structureClassModel.setName_test_class("String");
     structureClassModel.setAttributes(new LinkedList<>());
     structureClassModel.setBuilder(false);

     Assert.assertEquals("String", structureClassModel.getPackpage());
     Assert.assertEquals(new LinkedList<>(), structureClassModel.getImports());
     Assert.assertEquals(new LinkedList<>(), structureClassModel.getTags());
     Assert.assertEquals("String", structureClassModel.getName_class());
     Assert.assertEquals("String", structureClassModel.getName_test_class());
     Assert.assertEquals(new LinkedList<>(), structureClassModel.getAttributes());
     Assert.assertEquals(false, structureClassModel.isBuilder());
 }

}
