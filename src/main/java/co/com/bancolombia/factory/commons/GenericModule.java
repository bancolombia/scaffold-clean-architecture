package co.com.bancolombia.factory.commons;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.Constants.MainFiles.*;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class GenericModule {
  public static final String AWS_BOM =
      "\timplementation platform('software.amazon.awssdk:bom:" + Constants.AWS_BOM_VERSION + "')";
  public static final String AWS_BOM_KT =
      "\timplementation(platform(\"software.amazon.awssdk:bom:"
          + Constants.AWS_BOM_VERSION
          + "\"))";

  private GenericModule() {}

  public static void generateGenericModule(
      ModuleBuilder builder, String exceptionMessage, String baseDir, String template)
      throws IOException, CleanException {
    String name = builder.getStringParam("task-param-name");

    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException(exceptionMessage);
    }
    String dashName = Utils.toDashName(name);
    builder.addParam("name-dash", dashName);
    builder.addParam("name-package", name.toLowerCase().replaceAll("[-_]*", ""));
    builder.appendToSettings(dashName, baseDir);
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":" + dashName);
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    builder.setupFromTemplate(template);
  }

  public static void addAwsBom(ModuleBuilder builder) throws IOException, CleanException {
    if (builder.isKotlin()) {
      addAwsBomKotlin(builder);
    } else {
      addAwsBomJava(builder);
      if (builder.withMetrics()) {
        builder.addParam("task-param-name", "metrics");
        GenericModule.generateGenericModule(
            builder, null, "infrastructure/helpers", "helper/metrics/aws");
      }
    }
  }

  private static void addAwsBomJava(ModuleBuilder builder) throws IOException {
    builder.updateFile(
        MAIN_GRADLE,
        content -> {
          if (content.contains("software.amazon.awssdk")) {
            return content;
          }
          return Utils.addDependency(content, AWS_BOM);
        });
    builder.updateFile(
        APP_BUILD_GRADLE,
        content -> Utils.addDependency(content, "implementation 'software.amazon.awssdk:sts'"));
  }

  private static void addAwsBomKotlin(ModuleBuilder builder) throws IOException {
    builder.updateFile(
        BUILD_GRADLE_KTS,
        content -> {
          if (content.contains("software.amazon.awssdk")) {
            return content;
          }
          return Utils.addDependency(content, AWS_BOM_KT);
        });
    builder.updateFile(
        APP_BUILD_GRADLE_KTS,
        content -> Utils.addDependency(content, "implementation(\"software.amazon.awssdk:sts\")"));
  }
}
