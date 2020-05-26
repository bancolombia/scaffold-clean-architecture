package co.com.bancolombia.templates;

import co.com.bancolombia.Utils;
import co.com.bancolombia.models.AttributeClassModel;
import co.com.bancolombia.models.StructureClassModel;

import java.util.List;


public class UnitTestModelTemplate {

    public static String getUnitTestModel(StructureClassModel classModel){
        String unitTest = "";

        unitTest += classModel.getPackpage() + ";\n \n";
        unitTest += "import org.junit.Assert;\n";

        if(classModel.isBuilder())
            unitTest += "import org.junit.Before;\n";

        unitTest += "import org.junit.Test;\n\n";
        unitTest += (classModel.getAttributes().stream().parallel().filter(d -> d.getAClass().equals("Date")).findFirst().orElse(null) != null) ? "import java.util.Date;\n\n" : "";
        unitTest += (classModel.getAttributes().stream().parallel().filter(d -> d.getAClass().contains("List")).findFirst().orElse(null) != null) ? "import java.util.LinkedList;\n\n" : "";
        unitTest += "public class " + classModel.getName_test_class() + "{\n\n";
        String variable = Utils.decapitalize(classModel.getName_class());

        if(classModel.isBuilder()){

            unitTest += classModel.getName_class() + " " + variable +";\n\n";
            unitTest += "   @Before\n";
            unitTest += "   public void init(){\n";
            unitTest += "     " + variable + " = " + classModel.getName_class() + ".builder()\n";
            for(AttributeClassModel attr : classModel.getAttributes()){
                unitTest += "     ." + attr.getName() + "(" + Utils.getValuesByClass(attr.getAClass()) + ")\n";
            }
            unitTest += "     .build();\n";
            unitTest += "   }\n\n";

            unitTest += "   @Test\n";
            unitTest += "   public void " + variable + "Test(){\n";
            unitTest += getAsserts(classModel.getAttributes(), variable);
            unitTest += "   }\n\n";

        }else{
            if(classModel.getTags().contains("@NoArgsConstructor") || classModel.getTags().contains("@Data")){
                unitTest += "   @Test\n";
                unitTest += "   public void " + variable + "NoArgsTest(){\n";
                unitTest += "     " + classModel.getName_class() + " " + variable + " = new "+ classModel.getName_class()+"();\n";
                for(AttributeClassModel attr : classModel.getAttributes()) {
                    unitTest += "     "+variable+".set" + Utils.capitalize(attr.getName())+"("+Utils.getValuesByClass(attr.getAClass())+");\n";
                }
                unitTest += "\n";
                unitTest += getAsserts(classModel.getAttributes(), variable);
                unitTest += "   }\n\n";
            }

            if(classModel.getTags().contains("@AllArgsConstructor")){
                unitTest += "   @Test\n";
                unitTest += "   public void " + variable + "AllArgsTest(){\n";
                unitTest += "     " + classModel.getName_class() + " " + variable + " = new "+ classModel.getName_class()+"(\n";
                String params = "";
                for(AttributeClassModel attr : classModel.getAttributes()) {
                    params += "     "+Utils.getValuesByClass(attr.getAClass())+",\n";
                }
                unitTest += params.substring(0, params.length()-2);
                unitTest += ");\n\n";
                for(AttributeClassModel attr : classModel.getAttributes()) {
                    unitTest += "     "+variable+".set" + Utils.capitalize(attr.getName())+"("+Utils.getValuesByClass(attr.getAClass())+");\n";
                }
                unitTest += "\n";
                unitTest += getAsserts(classModel.getAttributes(), variable);
                unitTest += "   }\n\n";
            }
        }

        unitTest += "}";

        return unitTest;
    }

    private static String getAsserts(List<AttributeClassModel> attributes, String variable){
        String retorno = "";
        for(AttributeClassModel attr : attributes) {
            retorno += "     Assert.assertEquals(" + Utils.getValuesByClass(attr.getAClass()) + ", "+variable+"." + (attr.getAClass().equals("boolean")?"is":"get") + "" + Utils.capitalize(attr.getName())+"()"+ Utils.getDelta(attr.getAClass()) +");\n";
        }
        return retorno;
    }
}
