package io.swagger.codegen.v3.generators;

import com.samskivert.mustache.Mustache;
import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenType;
import io.swagger.codegen.v3.generators.java.AbstractJavaCodegen;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

@SuppressWarnings("unchecked")
public abstract class AbstractScaffoldCodegen extends AbstractJavaCodegen {
  static final String SUBTYPES = "x-subtypes";
  static final String HAS_SUBTYPES = "x-has-subtypes";
  static final String HAS_OWN_VARS = "x-has-own-vars";
  static final String HAS_INLINE_ENUMS = "x-has-inline-enums";
  // Not a CodegenModel property, so the template cannot pick the parent model's value instead
  private static final String SUBTYPE_NAME = "x-subtype-name";
  static final String DISCRIMINATOR_PROPERTY = "x-discriminator-property";
  private static final String DISCRIMINATOR_MAPPING = "x-discriminator-mapping";
  private static final String SCHEMAS_REF_PREFIX = "#/components/schemas/";

  protected AbstractScaffoldCodegen() {
    super();
  }

  @Override
  public String getDefaultTemplateDir() {
    return "commons/rest-from-swagger";
  }

  @Override
  public CodegenType getTag() {
    return CodegenType.SERVER;
  }

  @Override
  protected String getTemplateDir() {
    return getDefaultTemplateDir();
  }

  @Override
  public void processOpts() {
    super.processOpts();
    apiTestTemplateFiles.clear();
    apiDocTemplateFiles.clear();
    apiTemplateFiles.clear();

    importMapping.put("OffsetDateTime", "java.time.OffsetDateTime");
    modelDocTemplateFiles.clear();
    additionalProperties.put(
        "lambdaEscapeDoubleQuote",
        (Mustache.Lambda)
            (fragment, writer) ->
                writer.write(fragment.execute().replace("\"", Matcher.quoteReplacement("\\\""))));
    additionalProperties.put(
        "lambdaRemoveLineBreak",
        (Mustache.Lambda)
            (fragment, writer) -> writer.write(fragment.execute().replaceAll("[\\r\\n]", "")));
    additionalProperties.put(
        "lambdaScapeVoid",
        (Mustache.Lambda)
            (fragment, writer) -> writer.write(fragment.execute().replaceAll("[\\r\\n]", "")));
  }

  @Override
  public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
    Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
    if (operations != null) {
      List<CodegenOperation> ops = (List<CodegenOperation>) operations.get("operation");
      for (final CodegenOperation operation : ops) {
        if (operation.examples != null) {
          for (Map<String, String> example : operation.examples) {
            for (Map.Entry<String, String> entry : example.entrySet()) {
              // Replace " with \", \r, \n with \\r, \\n
              String val =
                  entry.getValue().replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
              entry.setValue(val);
            }
          }
        }
      }
    }

    return objs;
  }

  /**
   * Keeps the discriminator of {@code oneOf}/{@code anyOf} schemas as written in the contract:
   * codegen later renames its property and adds entries to its mapping.
   */
  @Override
  public CodegenModel fromModel(String name, Schema schema, Map<String, Schema> allSchemas) {
    Discriminator discriminator = schema == null ? null : schema.getDiscriminator();
    String property = discriminator == null ? null : discriminator.getPropertyName();
    Map<String, String> mapping =
        discriminator == null || discriminator.getMapping() == null
            ? Map.of()
            : new LinkedHashMap<>(discriminator.getMapping());
    CodegenModel model = super.fromModel(name, schema, allSchemas);
    if (model != null && property != null && isOneOfOrAnyOf(schema)) {
      model.getVendorExtensions().put(DISCRIMINATOR_PROPERTY, property);
      model.getVendorExtensions().put(DISCRIMINATOR_MAPPING, mapping);
    }
    return model;
  }

  /**
   * Lists the options of every {@code oneOf}/{@code anyOf} schema on its interface, so the
   * templates can declare them as Jackson subtypes, and flags the models whose class declares
   * fields.
   */
  @Override
  public Map<String, Object> postProcessAllModels(Map<String, Object> processedModels) {
    Map<String, Object> result = super.postProcessAllModels(processedModels);
    Map<String, CodegenModel> models = new HashMap<>();
    for (Object value : result.values()) {
      List<Map<String, Object>> modelList =
          (List<Map<String, Object>>) ((Map<String, Object>) value).get("models");
      if (modelList != null) {
        modelList.stream()
            .map(entry -> (CodegenModel) entry.get("model"))
            .filter(Objects::nonNull)
            .forEach(model -> models.put(model.classname, model));
      }
    }
    for (CodegenModel model : models.values()) {
      // Lombok creates the same no-args constructor for @AllArgsConstructor without fields
      model.getVendorExtensions().put(HAS_OWN_VARS, model.vars != null && !model.vars.isEmpty());
      // Codegen clears x-has-enums when it removes enums repeated from an allOf parent
      model
          .getVendorExtensions()
          .put(
              HAS_INLINE_ENUMS,
              model.vars != null && model.vars.stream().anyMatch(AbstractScaffoldCodegen::isEnum));
      // An enum option cannot implement the interface, so it cannot be a Jackson subtype
      if (model.interfaceModels == null || isEnum(model.getVendorExtensions())) {
        continue;
      }
      for (CodegenModel iface : model.interfaceModels) {
        CodegenModel target = models.getOrDefault(iface.classname, iface);
        Map<String, String> subtype = new LinkedHashMap<>();
        subtype.put("classname", model.classname);
        if (target.getVendorExtensions().containsKey(DISCRIMINATOR_PROPERTY)) {
          subtype.put(
              SUBTYPE_NAME,
              discriminatorValue(
                  model.name,
                  (Map<String, String>) target.getVendorExtensions().get(DISCRIMINATOR_MAPPING)));
        }
        ((List<Map<String, String>>)
                target.getVendorExtensions().computeIfAbsent(SUBTYPES, key -> new ArrayList<>()))
            .add(subtype);
        target.getVendorExtensions().put(HAS_SUBTYPES, true);
      }
    }
    models.values().stream()
        .map(model -> (List<Map<String, String>>) model.getVendorExtensions().get(SUBTYPES))
        .filter(Objects::nonNull)
        .forEach(subtypes -> subtypes.sort(Comparator.comparing(type -> type.get("classname"))));
    return result;
  }

  /** Whether the template renders an inner enum for the property or its (nested) items. */
  private static boolean isEnum(CodegenProperty property) {
    for (CodegenProperty current = property; current != null; current = current.items) {
      if (isEnum(current.getVendorExtensions())) {
        return true;
      }
    }
    return false;
  }

  private static boolean isEnum(Map<String, Object> vendorExtensions) {
    return Boolean.TRUE.equals(vendorExtensions.get(CodegenConstants.IS_ENUM_EXT_NAME));
  }

  private static boolean isOneOfOrAnyOf(Schema<?> schema) {
    return schema instanceof ComposedSchema composed
        && ((composed.getOneOf() != null && !composed.getOneOf().isEmpty())
            || (composed.getAnyOf() != null && !composed.getAnyOf().isEmpty()));
  }

  /** The mapping value that points to the schema or, as OpenAPI defines, the schema name. */
  private static String discriminatorValue(String schemaKey, Map<String, String> mapping) {
    return mapping.entrySet().stream()
        .filter(
            entry ->
                schemaKey.equals(entry.getValue())
                    || entry.getValue().endsWith(SCHEMAS_REF_PREFIX + schemaKey))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(schemaKey);
  }

  @Override
  public Map<String, Object> postProcessModels(Map<String, Object> objs) {
    List<HashMap<String, String>> imports = (List<HashMap<String, String>>) objs.get("imports");
    imports = imports.stream().filter(hash -> !hash.get("import").contains("io.swagger")).toList();
    objs.put("imports", imports);
    return super.postProcessModels(objs);
  }
}
