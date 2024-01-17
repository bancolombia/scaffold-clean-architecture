package co.com.bancolombia.models;

import java.util.List;
import lombok.Data;

@Data
public class UpdateSettings {
  private List<Dependency> maven;
  private List<Dependency> gradle;
  private List<Dependency> custom;

  @Data
  public static class Dependency {
    private String name;
    private String packageName;
    private String value;
  }
}
