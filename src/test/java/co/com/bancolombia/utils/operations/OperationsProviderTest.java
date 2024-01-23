package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OperationsProviderTest {

  @Test
  void shouldGetMockedWhenVariable() {
    // Act
    ExternalOperations operations = OperationsProvider.fromDefault();
    // Assert
    assertTrue(operations instanceof MockOperations);
  }

  @Test
  void shouldGetReal() {
    // Act
    ExternalOperations operations = OperationsProvider.real();
    // Assert
    assertTrue(operations instanceof HttpOperations);
  }
}
