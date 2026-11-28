package co.com.bancolombia.models;

import co.com.bancolombia.factory.upgrades.UpdateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  /** Finds quoted {@code group:artifact:version} declarations, one per string literal. */
  public static final String DECLARATION_REGEX =
      "['\"][^'\"\\s]+:[^'\"\\s]+:[^$'\"\\s][^'\"\\s]*['\"]";

  /**
   * Finds {@code id 'plugin' version 'x'} declarations; a version given by a variable, such as
   * {@code "$springBootVersion"}, is left out because it is not written here.
   */
  public static final String PLUGIN_DECLARATION_REGEX =
      "id\\s+['\"][^'\"\\s]+['\"]\\s+version\\s+['\"][^$'\"\\s][^'\"\\s]*['\"]";

  public String toRegex() {
    if (isGradlePlugin()) {
      return String.format(
          // A version given by a variable, such as "$sonarVersion", is not replaced
          "id\\s+['\"]%s['\"]\\s+version\\s+['\"][^$'\"\\s][^'\"\\s]*['\"]",
          Pattern.quote(this.getGroup()));
    }
    return String.format(
        "['\"]%s:%s:[^$'\"\\s][^'\"\\s]*['\"]",
        Pattern.quote(this.getGroup()), Pattern.quote(this.getArtifact()));
  }

  /**
   * Writes this release over the declarations of its artifact that are older, keeping their
   * classifier and in single quotes, as Gradle recommends for strings without variables. A
   * declaration that is the same or newer, such as a milestone, is not changed.
   */
  public String updateOlderDeclarations(String content) {
    return Pattern.compile(toRegex())
        .matcher(content)
        .replaceAll(match -> Matcher.quoteReplacement(updated(match.group())));
  }

  private String updated(String declaration) {
    String unquoted = declaration.replace("'", "").replace("\"", "");
    if (!isNewest(from(unquoted))) {
      return declaration;
    }
    if (isGradlePlugin()) {
      return toString();
    }
    String[] parts = unquoted.split(":", 4);
    String classifier = parts.length == 4 ? ":" + parts[3] : "";
    return "'" + getGroup() + ":" + getArtifact() + ":" + getVersion() + classifier + "'";
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

  /** Consistent with equals, so duplicated dependencies are removed in sets and distinct(). */
  @Override
  public int hashCode() {
    return Objects.hash(getGroup(), getArtifact());
  }

  public boolean isNewest(DependencyRelease current) {
    return UpdateUtils.isNewerVersion(current.getVersion(), this.getVersion());
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
      if (parts.length >= 3) {
        // A fourth part is a classifier, such as linux-x86_64, that is kept on update
        release.setVersion(parts[2]);
      }
    }
    return release;
  }
}
