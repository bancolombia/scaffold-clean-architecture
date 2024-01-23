package co.com.bancolombia.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class FileModelTest {

  @Test
  void fileModelToStringTest() {
    FileModel model = FileModel.builder().content("x").path("y").build();
    assertNotNull(model.toString());
  }
}
