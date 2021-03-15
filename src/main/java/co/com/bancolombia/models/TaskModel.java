package co.com.bancolombia.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskModel {
  private final String name;
  private final String shortcut;
  private final String description;
  private final String group;
  private final Class taskAction;
}
