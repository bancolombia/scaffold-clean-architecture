package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OperationsProviderTest {

  @Test
  public void shouldGetMockedWhenVariable() {
    // Act
    ExternalOperations operations = OperationsProvider.fromDefault();
    // Assert
    assertTrue(operations instanceof MockOperations);
  }

  @Test
  public void shouldGetReal() {
    // Act
    ExternalOperations operations = OperationsProvider.real();
    // Assert
    assertTrue(operations instanceof HttpOperations);
  }
}
