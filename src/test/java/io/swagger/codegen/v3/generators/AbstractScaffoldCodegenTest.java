package io.swagger.codegen.v3.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.codegen.v3.CodegenType;
import org.junit.jupiter.api.Test;

public class AbstractScaffoldCodegenTest {

  @Test
  void shouldGetType() {
    // Arrange
    AbstractScaffoldCodegen codegen = new SampleScaffoldCodegen();
    // Act
    CodegenType type = codegen.getTag();
    // Assert
    assertEquals(CodegenType.SERVER, type);
  }

  private static class SampleScaffoldCodegen extends AbstractScaffoldCodegen {

    @Override
    public String getName() {
      return "sample";
    }

    @Override
    public String getHelp() {
      return "sample helpd";
    }
  }
}
