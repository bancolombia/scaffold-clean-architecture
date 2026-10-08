package io.swagger.codegen.v3.generators;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

public final class OpenApiModelPackageResolver {

    public static final String BASE_MODEL_PACKAGE = "baseModelPackage";

    private static final Pattern REQUEST_OR_RESPONSE =
            Pattern.compile("request|response", Pattern.CASE_INSENSITIVE);


    private OpenApiModelPackageResolver() {}

    //Agrega el package respectivo a la ubicacion del modelo
    static String resolvePackage(String baseModelPackage, String className) {
        String segment = resolveFolderSegment(className);
        if (segment.isEmpty()) {
            return baseModelPackage;
        }
        return baseModelPackage + "." + segment;
    }

    //Separa los modelos en folders request y response respectivamente
    static String resolveFolderSegment(String className) {
        String lower = className.toLowerCase(Locale.ROOT);
        if (lower.contains("response")) {
            return "response";
        }
        return "request";
    }

    //Asigna nombre al archivo
    static String modelFilePath(String originalClassName, String domainClassName) {
        String segment = resolveFolderSegment(originalClassName);
        if (segment.isEmpty()) {
            return domainClassName;
        }
        return segment + File.separator + domainClassName;
    }


    //Limpia los nombres de los modelos para cumplir arquitectura limpia
    static String cleanModelName(String className){
        if (!REQUEST_OR_RESPONSE.matcher(className).find()) {
            return className;
        }
        return REQUEST_OR_RESPONSE.matcher(className).replaceAll("").trim();
    }
}