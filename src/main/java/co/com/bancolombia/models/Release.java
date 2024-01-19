package co.com.bancolombia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Release {
  @JsonProperty("tag_name")
  private String tagName;

  @JsonProperty("published_at")
  private OffsetDateTime publishedAt;

  public Release cleanTagName() {
    return new Release(getTagName().replace("v", ""), getPublishedAt());
  }
}
