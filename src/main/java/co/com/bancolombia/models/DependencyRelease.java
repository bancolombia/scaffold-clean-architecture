package co.com.bancolombia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
@JsonDeserialize(using = DependencyReleasesDeserializer.class)
public class DependencyRelease {
  @JsonProperty("v")
  String version;

  @JsonProperty("g")
  String group;

  @JsonProperty("a")
  String artifact;
}
