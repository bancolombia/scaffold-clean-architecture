package co.com.bancolombia.task;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.adapters.DrivenAdapterRedis;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter.DrivenAdapterType;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class GenerateDrivenAdapterTask extends CleanArchitectureDefaultTask {
  private DrivenAdapterType type;
  private String name;
  private String url = "http://localhost:8080";
  private DrivenAdapterRedis.Mode mode = DrivenAdapterRedis.Mode.TEMPLATE;
  private BooleanOption secret = BooleanOption.FALSE;

  @Option(option = "type", description = "Set type of driven adapter to be generated")
  public void setType(DrivenAdapterType type) {
    this.type = type;
  }

  @Option(option = "name", description = "Set driven adapter name when GENERIC type")
  public void setName(String name) {
    this.name = name;
  }

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

  @OptionValues("type")
  public List<DrivenAdapterType> getTypes() {
    return Arrays.asList(DrivenAdapterType.values());
  }

  @OptionValues("secret")
  public List<BooleanOption> getSecretOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @TaskAction
  public void generateDrivenAdapterTask() throws IOException, CleanException {
    if (type == null) {
      printHelp();
      throw new IllegalArgumentException(
          "No Driven Adapter type is set, usage: gradle generateDrivenAdapter "
              + "--type "
              + Utils.formatTaskOptions(getTypes()));
    }
    ModuleFactory moduleFactory = ModuleFactoryDrivenAdapter.getDrivenAdapterFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Driven Adapter type: {}", type);
    builder.addParam("task-param-name", name);
    builder.addParam("include-secret", secret == BooleanOption.TRUE);
    builder.addParam(DrivenAdapterRedis.PARAM_MODE, mode);
    builder.addParam("lombok", builder.isEnableLombok());
    builder.addParam("task-param-url", url);
    moduleFactory.buildModule(builder);
    builder.persist();
  }
}
