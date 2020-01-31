package co.com.bancolombia.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskModel {
    private String name;
    private String shortcut;
    private String description;
    private String group;
    private Class taskAction;
}
