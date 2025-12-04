package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(
    name = "generatePipeline",
    shortcut = "gpl",
    description = "Generate CI pipeline as a code in deployment layer")
public class GeneratePipelineTask extends AbstractResolvableTypeTask {
  private BooleanOption monoRepo = BooleanOption.FALSE;

  @Option(option = "mono-repo", description = "is Mono repository")
  public void setMonoRepo(BooleanOption monoRepo) {
    this.monoRepo = monoRepo;
  }

  @OptionValues("mono-repo")
  public List<BooleanOption> getMonoRepoOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Override
  protected void prepareParams() {
    builder.addParam("monoRepo", monoRepo == BooleanOption.TRUE);
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
