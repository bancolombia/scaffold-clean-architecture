package co.com.bancolombia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonDeserialize(using = DependencyReleasesDeserializer.class)
public class DependencyRelease {
  @JsonProperty("v")
  private String version;

  @JsonProperty("g")
  private String group;

  @JsonProperty("a")
  private String artifact;

  @Override
  public String toString() {
    return String.format("'%s:%s:%s'", this.getGroup(), this.getArtifact(), this.getVersion());
  }

  public String toRegex() {
    return String.format("['\"]%s:%s:[^\\$].+['\"]", this.getGroup(), this.getArtifact());
  }

  public boolean valid() {
    return getGroup() != null && getArtifact() != null;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DependencyRelease) {
      DependencyRelease rel = (DependencyRelease) obj;
      return rel.valid()
          && valid()
          && rel.getGroup().equals(getGroup())
          && rel.getArtifact().equals(getArtifact());
    }
    return super.equals(obj);
  }

  public static DependencyRelease from(String dependency) {
    DependencyRelease release = new DependencyRelease();
    String[] parts = dependency.split(":");
    if (parts.length >= 2) {
      release.setGroup(parts[0]);
      release.setArtifact(parts[1]);
    }
    if (parts.length == 3) {
      release.setVersion(parts[2]);
    }
    return release;
  }
}
