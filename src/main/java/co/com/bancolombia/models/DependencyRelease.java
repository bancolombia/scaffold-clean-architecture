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
    return String.format("%s:%s:%s", this.getGroup(), this.getArtifact(), this.getVersion());
  }
}
