package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DomainModelCodegen extends AbstractScaffoldCodegen {
  static final String SUPER_BUILDER = "x-use-super-builder";
  static final String IMPLEMENTS_CLAUSE = "x-implements-clause";
  private static final Pattern GENERIC_TYPE = Pattern.compile("(\\w+)<(.+)>");
  private Set<String> requestSchemaKeys = Set.of();
  private Set<String> responseSchemaKeys = Set.of();

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
    requestSchemaKeys =
        OpenApiModelPackageResolver.parseSchemaKeys(
            additionalProperties.get(OpenApiModelPackageResolver.REQUEST_SCHEMA_KEYS));
    responseSchemaKeys =
        OpenApiModelPackageResolver.parseSchemaKeys(
            additionalProperties.get(OpenApiModelPackageResolver.RESPONSE_SCHEMA_KEYS));
  }

  @Override
  public String toModelFilename(String name) {
    String className = super.toModelName(name);
    return OpenApiModelPackageResolver.modelFilePath(
        name, className, requestSchemaKeys, responseSchemaKeys);
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
      String modelPackage =
          OpenApiModelPackageResolver.resolvePackage(
              baseModelPackage, schemaKey, requestSchemaKeys, responseSchemaKeys);
      openapiClassToDomainClass.put(openapiClassName, openapiClassName);
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
      String modelPackage = schemaKeyToPackage.get(schemaKey);
      Map<String, Object> models = (Map<String, Object>) entry.getValue();
      models.put("package", modelPackage);
      models.put("classname", openapiClassName);
      List<Map<String, Object>> modelList = (List<Map<String, Object>>) models.get("models");
      if (modelList == null) {
        continue;
      }
      for (Map<String, Object> modelEntry : modelList) {
        CodegenModel codegenModel = (CodegenModel) modelEntry.get("model");
        if (codegenModel == null) {
          continue;
        }
        codegenModel.classname = openapiClassName;
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
        applyImplementsClause(codegenModel, openapiClassToDomainClass);
        codegenModel.vars.forEach(this::clearDefaultsForLombokBuilder);
        allModels.add(codegenModel);
      }
    }
    markInheritance(allModels, getOpenAPI());
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

  private void clearDefaultsForLombokBuilder(CodegenProperty property) {
    if ("null".equals(property.defaultValue)) {
      property.defaultValue = null;
    }
    if (Boolean.TRUE.equals(property.getIsContainer()) && property.defaultValue != null) {
      property.defaultValue = null;
    }
  }

  private void applyImplementsClause(
      CodegenModel model, Map<String, String> openapiClassToDomainClass) {
    if (model.interfaceModels == null || model.interfaceModels.isEmpty()) {
      return;
    }
    String clause =
        model.interfaceModels.stream()
            .map(iface -> openapiClassToDomainClass.getOrDefault(iface.classname, iface.classname))
            .collect(Collectors.joining(", ", " implements ", ""));
    model.getVendorExtensions().put(IMPLEMENTS_CLAUSE, clause);
  }

  private void markInheritance(List<CodegenModel> models, OpenAPI openAPI) {
    Set<String> parentsInSpec =
        OpenApiModelSelectionResolver.allOfParentClassNames(openAPI, this::toModelName);
    for (CodegenModel model : models) {
      if (model.parent != null || parentsInSpec.contains(model.classname)) {
        model.getVendorExtensions().put(SUPER_BUILDER, true);
      }
    }
  }

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
      OpenAPI openAPI,
      Set<String> selectedSchemas,
      String baseModelPackage,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
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
              openapiClassName,
              OpenApiModelPackageResolver.resolvePackage(
                  baseModelPackage, schemaKey, requestSchemaKeys, responseSchemaKeys)));
    }
    return targets;
  }
}
