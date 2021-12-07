package co.com.bancolombia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Release {
  @JsonProperty("tag_name")
  private String tagName;

  @JsonProperty("published_at")
  private OffsetDateTime publishedAt;
}
