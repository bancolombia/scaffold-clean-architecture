package co.com.bancolombia.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TaskModelTest {

  @Test
  void taskModelToString() {
    TaskModel model = TaskModel.builder().name("").group("").shortcut("").build();

    assertNotNull(model.toString());
  }
}
