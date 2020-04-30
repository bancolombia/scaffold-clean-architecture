package co.com.bancolombia.models;

import org.junit.Assert;
import org.junit.Test;

public class TaskModelTest {

    @Test
    public void taskModelToString() {
        String model = TaskModel.builder().build().toString();
        Assert.assertNotNull(model);
    }

}
