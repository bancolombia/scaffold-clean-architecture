package co.com.bancolombia.models;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DependencyReleaseXml {
  private String version;
  private String groupId;
  private String artifactId;

  public Optional<DependencyRelease> toDependencyRelease() {
    if (getVersion() != null) {
      DependencyRelease release = new DependencyRelease();
      release.setArtifact(this.getArtifactId());
      release.setGroup(this.getGroupId());
      release.setVersion(this.getVersion());
      release.setGradlePlugin(true);
      return Optional.of(release);
    } else {
      return Optional.empty();
    }
  }
}
