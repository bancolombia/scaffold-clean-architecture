package io.swagger.codegen.v3.generators;

import com.samskivert.mustache.Mustache;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenType;
import io.swagger.codegen.v3.generators.java.AbstractJavaCodegen;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@SuppressWarnings("unchecked")
public abstract class AbstractScaffoldCodegen extends AbstractJavaCodegen {

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

  @Override
  public Map<String, Object> postProcessModels(Map<String, Object> objs) {
    List<HashMap<String, String>> imports = (List<HashMap<String, String>>) objs.get("imports");
    imports = imports.stream().filter(hash -> !hash.get("import").contains("io.swagger")).toList();
    objs.put("imports", imports);
    return super.postProcessModels(objs);
  }
}
