package co.com.bancolombia.models;

import co.com.bancolombia.task.GenerateStructureTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gradle.api.DefaultTask;

@Data
@AllArgsConstructor
public class TaskModel {
    String name;
    String shortcut;
    String description;
    String group;
    Class taskAction;



}
