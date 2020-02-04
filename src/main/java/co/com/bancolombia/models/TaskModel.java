package co.com.bancolombia.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskModel {
    private String name;
    private String shortcut;
    private String description;
    private String group;
    private Class taskAction;
}
