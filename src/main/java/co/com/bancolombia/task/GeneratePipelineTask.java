package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;
import org.gradle.work.DisableCachingByDefault;

@CATask(
    name = "generatePipeline",
    shortcut = "gpl",
    description = "Generate CI pipeline as a code in deployment layer")
@DisableCachingByDefault(because = "Generates and modifies project files, it has side effects")
public abstract class GeneratePipelineTask extends AbstractResolvableTypeTask {
  private BooleanOption monoRepo = BooleanOption.FALSE;

  @Option(option = "mono-repo", description = "is Mono repository")
  public void setMonoRepo(BooleanOption monoRepo) {
    this.monoRepo = monoRepo;
  }

  @OptionValues("mono-repo")
  public List<BooleanOption> getMonoRepoOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  /** Whether the project uses the OpenTelemetry agent, so the pipeline must ship it. */
  @Input
  public abstract Property<Boolean> getOtel();

  @Override
  protected void prepareParams() {
    builder.addParam("monoRepo", monoRepo == BooleanOption.TRUE);
    builder.addParam("otel", getOtel().getOrElse(false));
  }

  @Override
  protected String resolvePrefix() {
    return "Pipeline";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.pipelines";
  }
}
