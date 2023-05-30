package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.PATH_GRAPHQL;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.factory.entrypoints.EntryPointRestMvcServer.Server;
import co.com.bancolombia.task.annotations.CATask;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(
    name = "generateEntryPoint",
    shortcut = "gep",
    description = "Generate entry point in infrastructure layer")
public class GenerateEntryPointTask extends AbstractResolvableTypeTask {
  private String pathGraphql = PATH_GRAPHQL;
  private Server server = Server.UNDERTOW;
  private BooleanOption router = BooleanOption.TRUE;
  private BooleanOption swagger = BooleanOption.FALSE;

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

  @OptionValues("router")
  public List<BooleanOption> getRoutersOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @OptionValues("swagger")
  public List<BooleanOption> getSwaggerOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Override
  protected void prepareParams() {
    builder.addParam("task-param-server", server);
    builder.addParam("task-param-pathgql", pathGraphql);
    builder.addParam("task-param-router", router == BooleanOption.TRUE);
    builder.addParam("include-swagger", swagger == BooleanOption.TRUE);
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
