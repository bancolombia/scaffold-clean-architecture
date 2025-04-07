package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class OperationsProviderTest {

  @Test
  void shouldGetMockedWhenVariable() {
    // Act
    ExternalOperations operations = OperationsProvider.fromDefault();
    // Assert
    assertInstanceOf(MockOperations.class, operations);
  }

  @Test
  void shouldGetReal() {
    // Act
    ExternalOperations operations = OperationsProvider.real();
    // Assert
    assertInstanceOf(HttpOperations.class, operations);
  }
}
