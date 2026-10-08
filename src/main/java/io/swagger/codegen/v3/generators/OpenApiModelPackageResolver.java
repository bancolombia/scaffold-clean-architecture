package io.swagger.codegen.v3.generators;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

public final class OpenApiModelPackageResolver {

    public static final String BASE_MODEL_PACKAGE = "baseModelPackage";

    private static final Pattern REQUEST_OR_RESPONSE =
            Pattern.compile("request|response", Pattern.CASE_INSENSITIVE);

    private static final Pattern DEFAULT_MODELS =
            Pattern.compile("meta|failure|error|UnauthorizedObject", Pattern.CASE_INSENSITIVE);


    private OpenApiModelPackageResolver() {}

    /**
     * Agrega el package respectivo a la clase
     * @param baseModelPackage nombre del package base
     * @param className nombre del modelo en el contrato
     * @return nombre del package base mas la carpeta correspondiente
     */
    static String resolvePackage(String baseModelPackage, String className) {
        String segment = resolveFolderSegment(className);
        if (segment.isEmpty()) {
            return baseModelPackage;
        }
        return baseModelPackage + "." + segment;
    }

    /** Separa los modelos en folders request y response respectivamente
     *
     * @param className Nombre del modelo
     * @return Dicta si va a carpteta request, response o ninguna basado en su nombre
     *
     */
    static String resolveFolderSegment(String className) {
        String lower = className.toLowerCase(Locale.ROOT);
        if (DEFAULT_MODELS.matcher(className).find()) {
            return "";
        }
        if (lower.contains("response")) {
            return "response";
        }
        return "request";
    }

    /**Asignael archivo en su carpeta correspondiente
     *
     * @param originalClassName nombre del modelo en el Contrato
     * @param domainClassName el nombre del archivo
     * @return
     */
    static String modelFilePath(String originalClassName, String domainClassName) {
        String segment = resolveFolderSegment(originalClassName);
        if (segment.isEmpty()) {
            return domainClassName;
        }
        return segment + File.separator + domainClassName;
    }


    /**
     * Limpia los nombres de los modelos para cumplir arquitectura limpia
     * @param className nombre del Modelo
     * @return nombre del modelo sin la palabre Response o Request
     */
    static String cleanModelName(String className){
        if (!REQUEST_OR_RESPONSE.matcher(className).find()) {
            return className;
        }
        return REQUEST_OR_RESPONSE.matcher(className).replaceAll("").trim();
    }
}