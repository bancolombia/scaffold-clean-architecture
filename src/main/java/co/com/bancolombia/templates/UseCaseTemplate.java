package co.com.bancolombia.templates;

import co.com.bancolombia.Utils;

public class UseCaseTemplate {

    public static final String BUILD_GRADLE_USE_CASE_CONTENT = "dependencies {\n" +
            "    compile project(':model')\n" +
            "}";

    private UseCaseTemplate(){}

    public static String getUseCase(String useCaseName, String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + "." + Constants.USECASE + "." + Utils.decapitalize(useCaseName) + ";\n" +
                "\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + Utils.capitalize(useCaseName) + " {\n" +
                "\n" +
                "}\n";
    }
}
