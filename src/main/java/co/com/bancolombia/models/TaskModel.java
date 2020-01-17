package co.com.bancolombia.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskModel {
    String name;
    String shortcut;
    String description;
    String group;
    Class taskAction;
}
