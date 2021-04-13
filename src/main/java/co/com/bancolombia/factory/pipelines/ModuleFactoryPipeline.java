package co.com.bancolombia.factory.pipelines;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;

public class ModuleFactoryPipeline {

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public static ModuleFactory getPipelineFactory(PipelineType type)
      throws InvalidTaskOptionException {
    switch (type) {
      case AZURE:
        return new PipelineAzure();
      case GITHUB:
        return new GitHubAction();
      default:
        throw new InvalidTaskOptionException("Pipeline value invalid");
    }
  }

  public enum PipelineType {
    AZURE,
    GITHUB
  }
}
