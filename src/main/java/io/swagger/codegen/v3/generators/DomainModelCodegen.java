package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DomainModelCodegen extends AbstractScaffoldCodegen {

  static final String SUPER_BUILDER = "x-use-super-builder";
  private static final Pattern GENERIC_TYPE = Pattern.compile("(\\w+)<(.+)>");

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
    return "commons/model-from-openapi";
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
    Map<String, String> schemaKeyToPackage = new HashMap<>();
    Map<String, String> openapiClassToPackage = new HashMap<>();
    for (String schemaKey : result.keySet()) {
      String openapiClassName = super.toModelName(schemaKey);
      String domainClassName = OpenApiModelPackageResolver.cleanModelName(openapiClassName);
      String modelPackage =
          OpenApiModelPackageResolver.resolvePackage(baseModelPackage, openapiClassName);
      openapiClassToDomainClass.put(openapiClassName, domainClassName);
      schemaKeyToPackage.put(schemaKey, modelPackage);
      openapiClassToPackage.put(openapiClassName, modelPackage);
    }

    Set<String> openapiModelImports =
        openapiClassToDomainClass.keySet().stream()
            .map(this::toModelImport)
            .collect(Collectors.toSet());

    List<CodegenModel> allModels = new ArrayList<>();
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String schemaKey = entry.getKey();
      String openapiClassName = super.toModelName(schemaKey);
      String domainClassName = OpenApiModelPackageResolver.cleanModelName(openapiClassName);
      String modelPackage = schemaKeyToPackage.get(schemaKey);
      Map<String, Object> models = (Map<String, Object>) entry.getValue();
      models.put("package", modelPackage);
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
        models.put(
            "imports",
            mergeImports(
                models.get("imports"),
                buildImports(
                    codegenModel, modelPackage, openapiClassToPackage, openapiClassToDomainClass),
                openapiModelImports));
        remapPropertyTypes(codegenModel, openapiClassToDomainClass);
        if (codegenModel.parent != null) {
          codegenModel.parent = remapType(codegenModel.parent, openapiClassToDomainClass);
        }
        codegenModel.vars.forEach(this::clearNullDefault);
        allModels.add(codegenModel);
      }
    }
    markInheritance(allModels);
    return result;
  }

  @SuppressWarnings("unchecked")
  private static List<Map<String, String>> mergeImports(
      Object codegenImports,
      List<Map<String, String>> domainImports,
      Set<String> openapiModelImports) {
    Map<String, Map<String, String>> merged = new LinkedHashMap<>();
    if (codegenImports instanceof List<?> list) {
      for (Object item : list) {
        Map<String, String> imp = (Map<String, String>) item;
        String fqcn = imp.get("import");
        if (fqcn != null && !openapiModelImports.contains(fqcn)) {
          merged.putIfAbsent(fqcn, imp);
        }
      }
    }
    domainImports.forEach(imp -> merged.putIfAbsent(imp.get("import"), imp));
    return new ArrayList<>(merged.values());
  }

  private void clearNullDefault(CodegenProperty property) {
    if ("null".equals(property.defaultValue)) {
      property.defaultValue = null;
    }
  }

  private void markInheritance(List<CodegenModel> models) {
    Set<String> parents =
        models.stream()
            .map(model -> model.parent)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    for (CodegenModel model : models) {
      if (model.parent != null || parents.contains(model.classname)) {
        model.getVendorExtensions().put(SUPER_BUILDER, true);
      }
    }
  }

  // Remap property types so they reference the cleaned domain class names.
  private void remapPropertyTypes(
      CodegenModel model, Map<String, String> openapiClassToDomainClass) {
    for (CodegenProperty property : model.allVars) {
      remapOneProperty(property, openapiClassToDomainClass);
    }
    if (model.vars != null) {
      for (CodegenProperty property : model.vars) {
        remapOneProperty(property, openapiClassToDomainClass);
      }
    }
  }

  // Remap property type so they reference the cleaned domain class names.
  private void remapOneProperty(CodegenProperty property, Map<String, String> map) {
    property.datatype = remapType(property.datatype, map);
    property.baseType = remapType(property.baseType, map);
    property.complexType = remapType(property.complexType, map);
    property.datatypeWithEnum = remapType(property.datatypeWithEnum, map);
    if (property.items != null) {
      property.items.datatype = remapType(property.items.datatype, map);
      property.items.baseType = remapType(property.items.baseType, map);
      property.items.datatypeWithEnum = remapType(property.items.datatypeWithEnum, map);
    }
  }

  private String remapType(String type, Map<String, String> openapiClassToDomainClass) {
    if (type == null || type.isBlank()) {
      return type;
    }
    Matcher generic = GENERIC_TYPE.matcher(type);
    if (generic.matches()) {
      return generic.group(1) + "<" + remapType(generic.group(2), openapiClassToDomainClass) + ">";
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

  /**
   * Package per schema is set in {@link #postProcessAllModels} via {@code models.put("package",
   * ...)}.
   */
  private List<Map<String, String>> buildImports(
      CodegenModel model,
      String modelPackage,
      Map<String, String> openapiClassToPackage,
      Map<String, String> openapiClassToDomainClass) {
    Set<String> importOpenapiTypes = new LinkedHashSet<>();
    if (model.parent != null) {
      importOpenapiTypes.add(model.parent);
    }
    for (CodegenProperty property : model.allVars) {
      collectReferencedOpenapiTypes(property.datatype, importOpenapiTypes);
      collectReferencedOpenapiTypes(property.baseType, importOpenapiTypes);
      collectReferencedOpenapiTypes(property.complexType, importOpenapiTypes);
      if (property.items != null) {
        collectReferencedOpenapiTypes(property.items.datatype, importOpenapiTypes);
        collectReferencedOpenapiTypes(property.items.baseType, importOpenapiTypes);
      }
    }

    List<Map<String, String>> imports = new ArrayList<>();
    for (String openapiType : importOpenapiTypes) {
      String refPackage = openapiClassToPackage.get(openapiType);
      if (refPackage == null || refPackage.equals(modelPackage)) {
        continue;
      }
      String domainType = openapiClassToDomainClass.getOrDefault(openapiType, openapiType);
      Map<String, String> entry = new HashMap<>();
      entry.put("import", refPackage + "." + domainType);
      imports.add(entry);
    }
    return imports;
  }

  private void collectReferencedOpenapiTypes(String datatype, Set<String> importOpenapiTypes) {
    if (datatype == null || datatype.isBlank()) {
      return;
    }
    Matcher generic = GENERIC_TYPE.matcher(datatype);
    if (generic.matches()) {
      collectReferencedOpenapiTypes(generic.group(1), importOpenapiTypes);
      collectReferencedOpenapiTypes(generic.group(2), importOpenapiTypes);
      return;
    }
    if (Character.isUpperCase(datatype.charAt(0))) {
      importOpenapiTypes.add(datatype);
    }
  }

  public record ModelTarget(String schemaKey, String className, String packageName) {
    public String relativePath() {
      return packageName.replace('.', '/') + "/" + className + ".java";
    }
  }

  public static List<ModelTarget> resolveTargets(
      OpenAPI openAPI, Set<String> selectedSchemas, String baseModelPackage) {
    DomainModelCodegen codegen = new DomainModelCodegen();
    Set<String> schemaKeys =
        openAPI.getComponents() == null || openAPI.getComponents().getSchemas() == null
            ? Set.of()
            : openAPI.getComponents().getSchemas().keySet();
    List<ModelTarget> targets = new ArrayList<>();
    for (String schemaKey : schemaKeys) {
      if (!selectedSchemas.isEmpty() && !selectedSchemas.contains(schemaKey)) {
        continue;
      }
      String openapiClassName = codegen.toModelName(schemaKey);
      targets.add(
          new ModelTarget(
              schemaKey,
              OpenApiModelPackageResolver.cleanModelName(openapiClassName),
              OpenApiModelPackageResolver.resolvePackage(baseModelPackage, openapiClassName)));
    }
    return targets;
  }
}
