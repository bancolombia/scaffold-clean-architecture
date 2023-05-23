package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.PATH_GRAPHQL;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.entrypoints.EntryPointRestMvcServer.Server;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class GenerateEntryPointTask extends CleanArchitectureDefaultTask {
  private String type;
  private String name;
  private String pathGraphql = PATH_GRAPHQL;
  private Server server = Server.UNDERTOW;
  private BooleanOption router = BooleanOption.TRUE;
  private BooleanOption swagger = BooleanOption.FALSE;

  @Option(option = "type", description = "Set type of entry point to be generated")
  public void setType(String type) {
    this.type = type;
  }

  @Option(option = "name", description = "Set entry point name when GENERIC type")
  public void setName(String name) {
    this.name = name;
  }

  @Option(
      option = "server",
      description = "Set server on which the application will run when RESTMVC type")
  public void setServer(Server server) {
    this.server = server;
  }

  @Option(option = "router", description = "Set router function for webflux ")
  public void setRouter(BooleanOption router) {
    this.router = router;
  }

  @Option(option = "swagger", description = "Set swagger configuration to rest entry point ")
  public void setSwagger(BooleanOption swagger) {
    this.swagger = swagger;
  }

  @Option(option = "pathgql", description = "set API GraphQL path")
  public void setPathGraphql(String pathgql) {
    this.pathGraphql = pathgql;
  }

  @OptionValues("server")
  public List<Server> getServerOptions() {
    return Arrays.asList(Server.values());
  }

  @OptionValues("type")
  public List<String> getTypes() {
    return super.resolveTypes();
  }

  @OptionValues("router")
  public List<BooleanOption> getRoutersOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @OptionValues("swagger")
  public List<BooleanOption> getSwaggerOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @TaskAction
  public void generateEntryPointTask() throws IOException, CleanException {
    long start = System.currentTimeMillis();
    if (type == null) {
      printHelp();
      throw new IllegalArgumentException(
          "No Entry Point is set, usage: gradle generateEntryPoint --type "
              + Utils.formatTaskOptions(getTypes()));
    }
    ModuleFactory moduleFactory = resolveFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Entry Point type: {}", type);
    builder.addParam("task-param-name", name);
    builder.addParam("task-param-server", server);
    builder.addParam("task-param-pathgql", pathGraphql);
    builder.addParam("task-param-router", router == BooleanOption.TRUE);
    builder.addParam("include-swagger", swagger == BooleanOption.TRUE);
    builder.addParam("lombok", builder.isEnableLombok());
    builder.addParam("metrics", builder.withMetrics());
    moduleFactory.buildModule(builder);
    builder.persist();
    sendAnalytics(type, System.currentTimeMillis() - start);
  }

  @Override
  protected String resolvePrefix() {
    return "EntryPoint";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.entrypoints";
  }
}
