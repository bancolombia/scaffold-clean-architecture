package co.com.bancolombia.task;

import co.com.bancolombia.factory.adapters.DrivenAdapterBinStash;
import co.com.bancolombia.factory.adapters.DrivenAdapterRedis;
import co.com.bancolombia.factory.adapters.DrivenAdapterSecrets;
import co.com.bancolombia.task.annotations.CATask;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(
    name = "generateDrivenAdapter",
    shortcut = "gda",
    description = "Generate driven adapter in infrastructure layer")
public class GenerateDrivenAdapterTask extends AbstractResolvableTypeTask {
  private String url = "http://localhost:8080";
  private String swaggerFile = null;
  private DrivenAdapterSecrets.SecretsBackend secretsBackend =
      DrivenAdapterSecrets.SecretsBackend.AWS_SECRETS_MANAGER;

  private DrivenAdapterRedis.Mode mode = DrivenAdapterRedis.Mode.TEMPLATE;
  private DrivenAdapterBinStash.CacheMode cacheMode = DrivenAdapterBinStash.CacheMode.LOCAL;

  private BooleanOption secret = BooleanOption.FALSE;
  private BooleanOption eda = BooleanOption.FALSE;
  private String tech = "rabbitmq";

  @Option(option = "url", description = "Set driven adapter url when RESTCONSUMER type")
  public void setUrl(String url) {
    this.url = url;
  }

  @Option(option = "mode", description = "Set template or repository mode when REDIS type")
  public void setMode(DrivenAdapterRedis.Mode mode) {
    this.mode = mode;
  }

  @Option(option = "secret", description = "Enable secrets for this driven adapter")
  public void setSecret(BooleanOption secret) {
    this.secret = secret;
  }

  @OptionValues("secret")
  public List<BooleanOption> getSecretOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Option(option = "eda", description = "Use EDA variant")
  public void setEda(BooleanOption eda) {
    this.eda = eda;
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

  @Option(option = "cache-mode", description = "Set value for cache type")
  public void setCacheMode(DrivenAdapterBinStash.CacheMode cacheMode) {
    this.cacheMode = cacheMode;
  }

  @Option(option = "from-swagger", description = "Generation will be from a swagger.yaml file")
  public void setFromSwagger(String swaggerFile) {
    this.swaggerFile = swaggerFile;
  }

  @Option(option = "secrets-backend", description = "Set secrets backend")
  public void setSecretsBackend(DrivenAdapterSecrets.SecretsBackend secretsBackend) {
    this.secretsBackend = secretsBackend;
  }

  @Option(option = "secretName", description = "Set the name of the secret in AWS Secrets Manager")
  public void setSecretName(String secretName) {
    builder.addParam("secretName", secretName);
  }

  @Override
  protected void prepareParams() {
    builder.addParam("task-param-cache-mode", cacheMode);
    builder.addParam("include-secret", secret == BooleanOption.TRUE);
    builder.addParam(DrivenAdapterRedis.PARAM_MODE, mode);
    builder.addParam("task-param-url", url);
    builder.addParam("swagger-file", swaggerFile);
    builder.addParam("secrets-backend", secretsBackend);
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
    return "DrivenAdapter";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.adapters";
  }
}
