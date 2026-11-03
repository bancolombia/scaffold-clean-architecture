package co.com.bancolombia.factory.commons;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class GenericModule {
  private static final String AWS_BOM_COORDINATE = "software.amazon.awssdk:bom";
  private static final String AWS_BOM_DEPENDENCY =
      "\timplementation platform('" + AWS_BOM_COORDINATE + ":" + Constants.AWS_BOM_VERSION + "')";

  private GenericModule() {}

  public static void generateGenericModule(
      ModuleBuilder builder, String exceptionMessage, String template)
      throws IOException, CleanException {
    String name = builder.getStringParam("task-param-name");

    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException(exceptionMessage);
    }
    String dashName = Utils.toDashName(name);
    builder.addParam("name-dash", dashName);
    builder.addParam("name-package", name.toLowerCase().replaceAll("[-_]*", ""));
    builder.setupFromTemplate(template);
  }

  public static void addAwsBom(ModuleBuilder builder) throws IOException, CleanException {
    enableAwsBom(builder);
    enableStsDependency(builder);
    if (builder.withMetrics()) {
      builder.addParam("task-param-name", "metrics");
      GenericModule.generateGenericModule(builder, null, "helper/metrics/aws");
    }
  }

  private static void enableAwsBom(ModuleBuilder builder) throws IOException {
    builder.updateFile(
        BUILD_GRADLE,
        content ->
            content.contains(AWS_BOM_COORDINATE)
                ? content
                : Utils.addDependency(content, AWS_BOM_DEPENDENCY));
  }

  private static void enableStsDependency(ModuleBuilder builder) throws IOException {
    builder.updateFile(
        APP_BUILD_GRADLE,
        content -> {
          if (content.contains("implementation 'software.amazon.awssdk:sts'")) {
            return content;
          }
          return Utils.addDependency(content, "implementation 'software.amazon.awssdk:sts'");
        });
  }
}
