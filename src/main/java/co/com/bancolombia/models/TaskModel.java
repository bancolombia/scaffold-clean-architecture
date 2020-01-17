package co.com.bancolombia.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskModel {
    String name;
    String shortcut;
    String description;
    String group;
    Class taskAction;
}
