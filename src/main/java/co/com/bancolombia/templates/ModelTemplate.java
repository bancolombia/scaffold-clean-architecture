package co.com.bancolombia.templates;

import co.com.bancolombia.Utils;

public class ModelTemplate {

    private ModelTemplate(){}

    public static String getModel(String modelName, String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + "." + Constants.MODEL + "." +
                Utils.decapitalize(modelName) + ";\n" +
                "\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "\n" +
                "@Data\n" +
                "@Builder(toBuilder = true)\n" +
                "public class " + Utils.capitalize(modelName) + "{\n" +
                "\n" +
                "\n" +
                "}\n";
    }

    public static String getInterfaceModel(String modelName, String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + "." + Constants.MODEL + "." + Utils.decapitalize(modelName) +
                "." + Constants.GATEWAYS + ";\n" +
                "\n" +
                "import " + packageName + "." + Constants.MODEL + "." + Utils.decapitalize(modelName) + "." +
                Utils.capitalize(modelName) + ";\n" +
                "\n" +
                "public interface " + Utils.capitalize(modelName) + "Repository " + "{\n" +
                "\n" +
                "\n" +
                "}\n";
    }
}
