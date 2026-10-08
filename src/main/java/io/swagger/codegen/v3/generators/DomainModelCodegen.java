package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainModelCodegen extends AbstractScaffoldCodegen {

    private static final Pattern GENERIC_TYPE = Pattern.compile("([A-Za-z0-9_]+)<(.+)>");

    public DomainModelCodegen() {
        super();
    }

    @Override
    public String getName() {
        return "DomainModel";
    }

    @Override
    public String getHelp() {
        return "Generates domain model classes from OpenAPI components.schemas (no API layer).";
    }

    @Override
    public String getDefaultTemplateDir() {
        return "commons/rest-from-swagger";
    }

    @Override
    public void processOpts() {
        super.processOpts();
        apiTemplateFiles.clear();
        apiTestTemplateFiles.clear();
        apiDocTemplateFiles.clear();
        supportingFiles.clear();
    }

    /**
     * File path only (folder + .java name). Do not override {@link #toModelName(String)}: Swagger
     * Codegen sorts models with a {@code TreeMap} on {@code toModelName(schemaKey)}; if two schemas
     * map to the same name (e.g. bill + billResponse → Bill), one schema is dropped and response/*
     * never appears.
     */
    @Override
    public String toModelFilename(String name) {
        String openapiClassName = super.toModelName(name);
        String domainClassName = OpenApiModelPackageResolver.cleanModelName(openapiClassName);
        return OpenApiModelPackageResolver.modelFilePath(openapiClassName, domainClassName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> postProcessAllModels(Map<String, Object> processedModels) {
        Map<String, Object> result = super.postProcessAllModels(processedModels);
        String baseModelPackage = getBaseModelPackage();

        Map<String, String> openapiClassToDomainClass = new HashMap<>();
        Map<String, String> classToPackage = new HashMap<>();

        for (String schemaKey : result.keySet()) {
            String openapiClassName = super.toModelName(schemaKey);
            String domainClassName = OpenApiModelPackageResolver.cleanModelName(openapiClassName);
            openapiClassToDomainClass.put(openapiClassName, domainClassName);
            classToPackage.put(
                    domainClassName,
                    OpenApiModelPackageResolver.resolvePackage(baseModelPackage, openapiClassName));
        }

        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String schemaKey = entry.getKey();
            String openapiClassName = super.toModelName(schemaKey);
            String domainClassName = OpenApiModelPackageResolver.cleanModelName(openapiClassName);

            Map<String, Object> models = (Map<String, Object>) entry.getValue();
            models.put("package", classToPackage.get(domainClassName));
            models.put("classname", domainClassName);

            List<Map<String, Object>> modelList = (List<Map<String, Object>>) models.get("models");
            if (modelList == null) {
                continue;
            }
            for (Map<String, Object> modelEntry : modelList) {
                CodegenModel codegenModel = (CodegenModel) modelEntry.get("model");
                if (codegenModel == null) {
                    continue;
                }
                codegenModel.classname = domainClassName;
                remapPropertyTypes(codegenModel, openapiClassToDomainClass);
                models.put(
                        "imports",
                        buildImports(codegenModel, classToPackage.get(domainClassName), classToPackage, openapiClassToDomainClass));
            }
        }
        return result;
    }

    private void remapPropertyTypes(CodegenModel model, Map<String, String> openapiClassToDomainClass) {
        for (CodegenProperty property : model.allVars) {
            property.datatype = remapType(property.datatype, openapiClassToDomainClass);
            property.baseType = remapType(property.baseType, openapiClassToDomainClass);
            property.complexType = remapType(property.complexType, openapiClassToDomainClass);
            if (property.items != null) {
                property.items.datatype = remapType(property.items.datatype, openapiClassToDomainClass);
                property.items.baseType = remapType(property.items.baseType, openapiClassToDomainClass);
            }
        }
    }

    private String remapType(String type, Map<String, String> openapiClassToDomainClass) {
        if (type == null || type.isBlank()) {
            return type;
        }
        Matcher generic = GENERIC_TYPE.matcher(type);
        if (generic.matches()) {
            return generic.group(1)
                    + "<"
                    + remapType(generic.group(2), openapiClassToDomainClass)
                    + ">";
        }
        return openapiClassToDomainClass.getOrDefault(type, type);
    }

    private String getBaseModelPackage() {
        Object base = additionalProperties.get(OpenApiModelPackageResolver.BASE_MODEL_PACKAGE);
        if (base != null && !base.toString().isBlank()) {
            return base.toString();
        }
        return modelPackage();
    }

    private List<Map<String, String>> buildImports(
            CodegenModel model,
            String modelPackage,
            Map<String, String> classToPackage,
            Map<String, String> openapiClassToDomainClass) {
        Set<String> importTypes = new LinkedHashSet<>();
        for (CodegenProperty property : model.allVars) {
            collectReferencedTypes(property.datatype, importTypes, openapiClassToDomainClass);
            collectReferencedTypes(property.baseType, importTypes, openapiClassToDomainClass);
            collectReferencedTypes(property.complexType, importTypes, openapiClassToDomainClass);
            if (property.items != null) {
                collectReferencedTypes(property.items.datatype, importTypes, openapiClassToDomainClass);
                collectReferencedTypes(property.items.baseType, importTypes, openapiClassToDomainClass);
            }
        }

        List<Map<String, String>> imports = new ArrayList<>();
        for (String type : importTypes) {
            String refPackage = classToPackage.get(type);
            if (refPackage == null || refPackage.equals(modelPackage)) {
                continue;
            }
            Map<String, String> entry = new HashMap<>();
            entry.put("import", refPackage + "." + type);
            imports.add(entry);
        }
        return imports;
    }

    private void collectReferencedTypes(
            String datatype, Set<String> importTypes, Map<String, String> openapiClassToDomainClass) {
        if (datatype == null || datatype.isBlank()) {
            return;
        }
        Matcher generic = GENERIC_TYPE.matcher(datatype);
        if (generic.matches()) {
            collectReferencedTypes(generic.group(1), importTypes, openapiClassToDomainClass);
            collectReferencedTypes(generic.group(2), importTypes, openapiClassToDomainClass);
            return;
        }
        String domainType = openapiClassToDomainClass.getOrDefault(datatype, datatype);
        if (Character.isUpperCase(domainType.charAt(0))) {
            importTypes.add(domainType);
        }
    }
}
