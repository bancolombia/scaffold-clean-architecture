package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import java.io.IOException;
import javax.inject.Inject;

public class HelperTask extends AbstractCleanArchitectureDefaultTask {
  public HelperTask() {
    builder.addParam("type", "JPA");
  }

  public String helperCheck(String check) {
    return builder.getStringParam(check);
  }

  public void setThrow(String value) {
    builder.addParam("throw", value);
  }

  @Inject
  public String getTaskPath() {
    throw new UnsupportedOperationException();
  }

  public void setTaskPath(String taskPath) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void execute() throws IOException, CleanException {}
}
