package co.com.bancolombia.models;

import static co.com.bancolombia.utils.Utils.isStableVersion;

import java.util.List;
import lombok.Data;

@Data
public class MavenMetadata {
  private String groupId;
  private String artifactId;
  private Versioning versioning;

  @Data
  public static class Versioning {
    private String latest;
    private String release;
    private List<String> versions;
    private String lastUpdated;
  }

  public DependencyRelease toDependencyRelease() {
    var release = new DependencyRelease();
    release.setGroup(this.groupId);
    release.setArtifact(this.artifactId);
    if (isStableVersion(this.versioning.getLatest())) {
      release.setVersion(this.versioning.getLatest());
      return release;
    }
    for (var i = this.versioning.versions.size() - 1; i >= 0; i--) {
      var version = this.versioning.versions.get(i);
      if (isStableVersion(version)) {
        release.setVersion(version);
        return release;
      }
    }
    return release;
  }
}
