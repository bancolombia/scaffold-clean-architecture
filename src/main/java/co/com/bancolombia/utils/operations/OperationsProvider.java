package co.com.bancolombia.utils.operations;

import co.com.bancolombia.utils.FileUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OperationsProvider {

  public static ExternalOperations real() {
    return new HttpOperations();
  }

  public static ExternalOperations fromDefault() {
    if (shouldMock()) {
      return new MockOperations();
    } else {
      return new HttpOperations();
    }
  }

  private boolean shouldMock() {
    return FileUtils.readBooleanProperty("simulateRest");
  }
}
