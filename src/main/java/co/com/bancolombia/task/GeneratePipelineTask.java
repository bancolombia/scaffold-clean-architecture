package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;

@CATask(
    name = "generatePipeline",
    shortcut = "gpl",
    description = "Generate CI pipeline as a code in deployment layer")
public class GeneratePipelineTask extends AbstractResolvableTypeTask {

  @Override
  protected void prepareParams() {
    // No additional params required
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
