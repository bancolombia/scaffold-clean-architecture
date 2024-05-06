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

  private boolean gradlePlugin = false;

  @Override
  public String toString() {
    if (isGradlePlugin()) {
      return String.format("id '%s' version '%s'", this.getGroup(), this.getVersion());
    }
    return String.format("'%s:%s:%s'", this.getGroup(), this.getArtifact(), this.getVersion());
  }

  public String toRegex() {
    if (isGradlePlugin()) {
      return String.format("id\\s+['\"]%s['\"]\\s+version\\s+['\"].+['\"]", this.getGroup());
    }
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
    if (dependency.startsWith("id") && dependency.contains("version")) {
      release.setGradlePlugin(true);
      String[] parts = dependency.split("id\\s+|\\s+version\\s+|\\s+");
      release.setGroup(parts[1]);
      release.setArtifact(parts[1] + ".gradle.plugin");
      release.setVersion(parts[2]);
    } else {
      String[] parts = dependency.split(":");
      if (parts.length >= 2) {
        release.setGroup(parts[0]);
        release.setArtifact(parts[1]);
      }
      if (parts.length == 3) {
        release.setVersion(parts[2]);
      }
    }
    return release;
  }
}
