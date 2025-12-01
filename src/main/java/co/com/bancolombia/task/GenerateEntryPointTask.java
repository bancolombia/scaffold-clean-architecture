package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.PATH_GRAPHQL;

import co.com.bancolombia.VersioningStrategy;
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
  private String swaggerFile = null;
  private Server server = Server.TOMCAT;
  private VersioningStrategy versioning = VersioningStrategy.NONE;
  private BooleanOption router = BooleanOption.TRUE;
  private BooleanOption swagger = BooleanOption.FALSE;
  private BooleanOption eda = BooleanOption.FALSE;
  private String tech = "rabbitmq";
  private BooleanOption authorization = BooleanOption.FALSE;

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

  @Option(option = "from-swagger", description = "Generation will be from a swagger.yaml file")
  public void setFromSwagger(String swaggerFile) {
    this.swaggerFile = swaggerFile;
  }

  @Option(
      option = "versioning",
      description = "define an api versioning strategy available only with router function")
  public void setVersioning(VersioningStrategy versioning) {
    this.versioning = versioning;
  }

  @Option(option = "pathgql", description = "set API GraphQL path")
  public void setPathGraphql(String pathgql) {
    this.pathGraphql = pathgql;
  }

  @Option(option = "authorization", description = "Enable authorization requests through a JWT")
  public void setAuthorization(BooleanOption authorization) {
    this.authorization = authorization;
  }

  @Option(option = "eda", description = "Use EDA variant")
  public void setEda(BooleanOption eda) {
    this.eda = eda;
  }

  @Option(option = "topicConsumer", description = "Set the topic for the Kafka consumer")
  public void setTopicConsumer(String topicConsumer) {
    builder.addParam("topicConsumer", topicConsumer);
  }

  @OptionValues("eda")
  public List<BooleanOption> getEdaOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Option(option = "tech", description = "Reactive Commons Technologies")
  public void setTech(String tech) {
    this.tech = tech;
  }

  @OptionValues("tech")
  public List<String> getTechOptions() {
    return Arrays.asList("kafka", "rabbitmq", "kafka,rabbitmq");
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

  @OptionValues("authorization")
  public List<BooleanOption> getAuthorizeOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @OptionValues("versioning")
  public List<VersioningStrategy> getVersioningOptions() {
    return Arrays.asList(VersioningStrategy.values());
  }

  @Override
  protected void prepareParams() {
    builder.addParam("task-param-server", server);
    builder.addParam("task-param-versioning-strategy", versioning);
    builder.addParam("task-param-pathgql", pathGraphql);
    builder.addParam("task-param-router", router == BooleanOption.TRUE);
    builder.addParam("task-param-authorize", authorization == BooleanOption.TRUE);
    builder.addParam("include-swagger", swagger == BooleanOption.TRUE);
    builder.addParam("swagger-file", swaggerFile);
    appendRCommonsParams();
  }

  private void appendRCommonsParams() {
    String[] techs = tech.split(",");

    for (String t : techs) {
      builder.addParam(t, true);
    }
    builder.addParam("eda", eda == BooleanOption.TRUE);
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
