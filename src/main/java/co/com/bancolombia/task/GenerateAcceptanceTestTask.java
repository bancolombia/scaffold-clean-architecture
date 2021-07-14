package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.tests.ModuleFactoryTests;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import org.gradle.api.tasks.TaskAction;

public class GenerateAcceptanceTestTask extends CleanArchitectureDefaultTask {

  @TaskAction
  public void generateAcceptanceTestTask() throws IOException, CleanException {

    ModuleFactory acceptanceTestFactory = ModuleFactoryTests.getTestsFactory();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    acceptanceTestFactory.buildModule(builder);
    builder.persist();
  }
}
