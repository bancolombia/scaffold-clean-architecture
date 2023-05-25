package co.com.bancolombia.task;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.factory.adapters.DrivenAdapterBinStash;
import co.com.bancolombia.factory.adapters.DrivenAdapterRedis;
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
  private DrivenAdapterRedis.Mode mode = DrivenAdapterRedis.Mode.TEMPLATE;
  private DrivenAdapterBinStash.CacheMode cacheMode = DrivenAdapterBinStash.CacheMode.LOCAL;

  private BooleanOption secret = BooleanOption.FALSE;

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

  @Option(option = "cache-mode", description = "Set value for cache type")
  public void setCacheMode(DrivenAdapterBinStash.CacheMode cacheMode) {
    this.cacheMode = cacheMode;
  }

  @Override
  protected void prepareParams() {
    builder.addParam("task-param-cache-mode", cacheMode);
    builder.addParam("include-secret", secret == BooleanOption.TRUE);
    builder.addParam(DrivenAdapterRedis.PARAM_MODE, mode);
    builder.addParam("task-param-url", url);
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
