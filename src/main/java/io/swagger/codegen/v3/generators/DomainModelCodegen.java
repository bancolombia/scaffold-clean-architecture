package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
  static final String CONTAINER_MODEL = "x-is-container-model";
  static final String IMPLEMENTS_CLAUSE = "x-implements-clause";
  private static final Pattern GENERIC_TYPE = Pattern.compile("(\\w+)<(.+)>");
  private Set<String> requestSchemaKeys = Set.of();
  private Set<String> responseSchemaKeys = Set.of();
  private Map<String, String> domainClassNames = Map.of();

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
    domainClassNames =
        readDomainClassNames(additionalProperties.get(OpenApiDomainModelNaming.DOMAIN_CLASS_NAMES));
  }

  /**
   * Every type name codegen derives from a schema key (class name, property types, parent,
   * interfaces) goes through here, so the domain renaming applies everywhere at once.
   */
  @Override
  public String toModelName(String name) {
    String domainClassName = domainClassNames.get(name);
    return domainClassName != null ? domainClassName : super.toModelName(name);
  }

  /** Codegen class name of a schema key, before the domain renaming. */
  String defaultModelName(String name) {
    return super.toModelName(name);
  }

  @Override
  public String toModelFilename(String name) {
    return OpenApiModelPackageResolver.modelFilePath(
        name, toModelName(name), requestSchemaKeys, responseSchemaKeys);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> postProcessAllModels(Map<String, Object> processedModels) {
    Map<String, Object> result = super.postProcessAllModels(processedModels);
    String baseModelPackage = getBaseModelPackage();
    Map<String, String> schemaKeyToPackage = new HashMap<>();
    Map<String, String> classNameToPackage = new HashMap<>();
    for (String schemaKey : result.keySet()) {
      String modelPackage =
          OpenApiModelPackageResolver.resolvePackage(
              baseModelPackage, schemaKey, requestSchemaKeys, responseSchemaKeys);
      schemaKeyToPackage.put(schemaKey, modelPackage);
      classNameToPackage.put(toModelName(schemaKey), modelPackage);
    }
    // codegen imports every model from the base package; the right ones are added below
    Set<String> baseModelImports =
        classNameToPackage.keySet().stream().map(this::toModelImport).collect(Collectors.toSet());
    List<CodegenModel> allModels = new ArrayList<>();
    for (Map.Entry<String, Object> entry : result.entrySet()) {
      String modelPackage = schemaKeyToPackage.get(entry.getKey());
      Map<String, Object> models = (Map<String, Object>) entry.getValue();
      models.put("package", modelPackage);
      List<Map<String, Object>> modelList = (List<Map<String, Object>>) models.get("models");
      if (modelList == null) {
        continue;
      }
      for (Map<String, Object> modelEntry : modelList) {
        CodegenModel codegenModel = (CodegenModel) modelEntry.get("model");
        if (codegenModel == null) {
          continue;
        }
        models.put(
            "imports",
            mergeImports(
                models.get("imports"),
                buildImports(codegenModel, modelPackage, classNameToPackage),
                baseModelImports));
        applyImplementsClause(codegenModel);
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
      Set<String> baseModelImports) {
    Map<String, Map<String, String>> merged = new LinkedHashMap<>();
    if (codegenImports instanceof List<?> list) {
      for (Object item : list) {
        Map<String, String> imp = (Map<String, String>) item;
        String fqcn = imp.get("import");
        if (fqcn != null && !baseModelImports.contains(fqcn)) {
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

  private void applyImplementsClause(CodegenModel model) {
    if (model.interfaceModels == null || model.interfaceModels.isEmpty()) {
      return;
    }
    String clause =
        model.interfaceModels.stream()
            .map(iface -> iface.classname)
            .collect(Collectors.joining(", ", " implements ", ""));
    model.getVendorExtensions().put(IMPLEMENTS_CLAUSE, clause);
  }

  private void markInheritance(List<CodegenModel> models, OpenAPI openAPI) {
    Set<String> parentsInSpec =
        OpenApiModelSelectionResolver.allOfParentClassNames(openAPI, this::toModelName);
    Map<String, CodegenModel> byClassName = new HashMap<>();
    models.forEach(model -> byClassName.put(model.classname, model));
    for (CodegenModel model : models) {
      if (isContainerModel(model)) {
        // Extends ArrayList or HashMap, which have no Lombok builder to extend
        model.getVendorExtensions().put(CONTAINER_MODEL, true);
      } else if ((model.parent != null || parentsInSpec.contains(model.classname))
          && !extendsContainerModel(model, byClassName)) {
        // @SuperBuilder needs a @SuperBuilder on every superclass; the others keep @Builder
        model.getVendorExtensions().put(SUPER_BUILDER, true);
      }
    }
  }

  private static boolean extendsContainerModel(
      CodegenModel model, Map<String, CodegenModel> byClassName) {
    Set<String> visited = new HashSet<>();
    for (CodegenModel current = byClassName.get(model.parent);
        current != null && visited.add(current.classname);
        current = byClassName.get(current.parent)) {
      if (isContainerModel(current)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isContainerModel(CodegenModel model) {
    Map<String, Object> extensions = model.getVendorExtensions();
    return Boolean.TRUE.equals(extensions.get(CodegenConstants.IS_ARRAY_MODEL_EXT_NAME))
        || Boolean.TRUE.equals(extensions.get(CodegenConstants.IS_MAP_CONTAINER_EXT_NAME));
  }

  private String getBaseModelPackage() {
    Object base = additionalProperties.get(OpenApiModelPackageResolver.BASE_MODEL_PACKAGE);
    if (base != null && !base.toString().isBlank()) {
      return base.toString();
    }
    return modelPackage();
  }

  private static List<Map<String, String>> buildImports(
      CodegenModel model, String modelPackage, Map<String, String> classNameToPackage) {
    Set<String> referencedTypes = new LinkedHashSet<>();
    collectReferencedTypes(model.parent, referencedTypes);
    if (model.interfaceModels != null) {
      model.interfaceModels.forEach(iface -> referencedTypes.add(iface.classname));
    }
    for (CodegenProperty property : model.allVars) {
      collectReferencedTypes(property.datatype, referencedTypes);
      collectReferencedTypes(property.baseType, referencedTypes);
      collectReferencedTypes(property.complexType, referencedTypes);
      if (property.items != null) {
        collectReferencedTypes(property.items.datatype, referencedTypes);
        collectReferencedTypes(property.items.baseType, referencedTypes);
      }
    }
    List<Map<String, String>> imports = new ArrayList<>();
    for (String type : referencedTypes) {
      String refPackage = classNameToPackage.get(type);
      if (refPackage == null || refPackage.equals(modelPackage)) {
        continue;
      }
      Map<String, String> entry = new HashMap<>();
      entry.put("import", refPackage + "." + type);
      imports.add(entry);
    }
    return imports;
  }

  private static void collectReferencedTypes(String type, Set<String> referencedTypes) {
    if (type == null || type.isBlank()) {
      return;
    }
    String trimmed = type.trim();
    Matcher generic = GENERIC_TYPE.matcher(trimmed);
    if (!generic.matches()) {
      referencedTypes.add(trimmed);
      return;
    }
    referencedTypes.add(generic.group(1));
    for (String argument : splitTypeArguments(generic.group(2))) {
      collectReferencedTypes(argument, referencedTypes);
    }
  }

  /** Splits {@code String, List<Pet>} on the commas that are not nested in another generic. */
  static List<String> splitTypeArguments(String arguments) {
    List<String> parts = new ArrayList<>();
    int depth = 0;
    int start = 0;
    for (int i = 0; i < arguments.length(); i++) {
      char c = arguments.charAt(i);
      if (c == '<') {
        depth++;
      } else if (c == '>') {
        depth--;
      } else if (c == ',' && depth == 0) {
        parts.add(arguments.substring(start, i));
        start = i + 1;
      }
    }
    parts.add(arguments.substring(start));
    return parts;
  }

  private static Map<String, String> readDomainClassNames(Object raw) {
    if (!(raw instanceof Map<?, ?> map)) {
      return Map.of();
    }
    Map<String, String> names = new HashMap<>();
    map.forEach((key, value) -> names.put(String.valueOf(key), String.valueOf(value)));
    return names;
  }

  /** Domain class name of every schema key: renames first, then forbidden suffixes stripped. */
  public static Map<String, String> resolveClassNames(
      OpenAPI openAPI, Map<String, String> renames) {
    DomainModelCodegen codegen = new DomainModelCodegen();
    return OpenApiDomainModelNaming.resolveClassNames(
        componentsSchemas(openAPI).keySet(), renames, codegen::defaultModelName);
  }

  public record ModelTarget(String schemaKey, String className, String packageName) {
    public String relativePath() {
      return packageName.replace('.', '/') + "/" + className + ".java";
    }
  }

  /** Files codegen will write: one per schema, except type aliases, which codegen skips. */
  public static List<ModelTarget> resolveTargets(
      OpenAPI openAPI,
      Set<String> selectedSchemas,
      String baseModelPackage,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys,
      Map<String, String> classNames) {
    List<ModelTarget> targets = new ArrayList<>();
    for (Map.Entry<String, Schema> entry : componentsSchemas(openAPI).entrySet()) {
      String schemaKey = entry.getKey();
      if (!selectedSchemas.isEmpty() && !selectedSchemas.contains(schemaKey)) {
        continue;
      }
      if (isTypeAlias(entry.getValue())) {
        continue;
      }
      targets.add(
          new ModelTarget(
              schemaKey,
              classNames.get(schemaKey),
              OpenApiModelPackageResolver.resolvePackage(
                  baseModelPackage, schemaKey, requestSchemaKeys, responseSchemaKeys)));
    }
    return targets;
  }

  /** Same rule codegen uses to treat a schema as an alias of a primitive type. */
  private static boolean isTypeAlias(Schema<?> schema) {
    if (schema == null || schema instanceof ArraySchema || schema instanceof MapSchema) {
      return false;
    }
    return schema.getType() != null
        && !"object".equals(schema.getType())
        && schema.getEnum() == null;
  }

  private static Map<String, Schema> componentsSchemas(OpenAPI openAPI) {
    if (openAPI == null
        || openAPI.getComponents() == null
        || openAPI.getComponents().getSchemas() == null) {
      return Map.of();
    }
    return openAPI.getComponents().getSchemas();
  }
}
