package co.com.bancolombia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class Release {
  @JsonProperty("tag_name")
  String tagName;

  @JsonProperty("published_at")
  OffsetDateTime publishedAt;
}
