package co.com.bancolombia.models;

import org.junit.Assert;
import org.junit.Test;

public class TaskModelTest {

  @Test
  public void taskModelToString() {
    TaskModel model = TaskModel.builder().name("").group("").shortcut("").build();

    Assert.assertNotNull(model.toString());
  }
}
