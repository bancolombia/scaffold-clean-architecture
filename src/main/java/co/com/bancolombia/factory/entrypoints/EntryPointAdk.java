package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointAdk implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    boolean enableMultiAgent = builder.getBooleanParam("adk-enable-multi-agent");
    boolean enableDevUi = builder.getBooleanParam("adk-enable-dev-ui");

    builder.addParam("adk-enable-multi-agent", enableMultiAgent);
    builder.addParam("adk-enable-dev-ui", enableDevUi);

    builder.setupFromTemplate("entry-point/adk");

    builder.appendToSettings("adk-agent", "infrastructure/entry-points");

    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":adk-agent"));
    builder.appendDependencyToModule(
        APP_SERVICE, "implementation 'org.springframework.boot:spring-boot-starter-webflux'");
    builder.appendDependencyToModule(
        APP_SERVICE, "implementation 'org.springframework.boot:spring-boot-starter-actuator'");

    String agentName = builder.getStringParam("task-param-name");
    if (agentName == null || agentName.isBlank()) {
      agentName = builder.getProjectName();
    }

    builder.appendToProperties("spring.application").put("name", agentName);
    builder
        .appendToProperties("adk.agent")
        .put("name", agentName)
        .put("model", "${ADK_MODEL:gemini-2.0-flash}")
        .put(
            "instruction",
            "You are a helpful assistant called '"
                + agentName
                + "'. Use available tools to help users.");

    builder.appendToProperties("adk.session").put("type", "${ADK_SESSION_TYPE:in-memory}");
  }
}
