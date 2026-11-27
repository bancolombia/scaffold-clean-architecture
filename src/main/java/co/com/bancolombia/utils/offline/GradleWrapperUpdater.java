package co.com.bancolombia.utils.offline;

import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/** Updates the Gradle wrapper configuration declared in a build.gradle file. */
@RequiredArgsConstructor
public class GradleWrapperUpdater {
  private static final Logger LOGGER = Logging.getLogger(GradleWrapperUpdater.class);
  private static final Pattern CURRENT_VERSION =
      Pattern.compile("gradleVersion\\s*=\\s*['\"]([^'\"]+)['\"]");
  private static final Pattern SHA256 = Pattern.compile("distributionSha256Sum\\s*=\\s*.*");
  private static final Pattern DISTRIBUTION_TYPE_ALL =
      Pattern.compile("distributionType\\s*=\\s*(Wrapper\\.)?DistributionType\\.ALL");

  private final ExternalOperations operations;

  /**
   * Sets the given gradle version and, only when the file already declares a checksum, the checksum
   * of that version. Version and checksum must change together, otherwise the wrapper download
   * fails, so nothing is changed when the checksum can't be resolved.
   */
  public String update(String content, String version) {
    if (!content.contains("wrapper") || !isNewer(content, version)) {
      return content;
    }
    if (!SHA256.matcher(content).find()) {
      return replaceVersion(content, version);
    }
    String type = DISTRIBUTION_TYPE_ALL.matcher(content).find() ? "all" : "bin";
    return operations
        .findGradleDistributionSha256(version, type)
        .valueOrWarn(LOGGER, "the checksum of Gradle " + version + "-" + type)
        .map(
            sha256 ->
                SHA256
                    .matcher(replaceVersion(content, version))
                    .replaceAll(
                        Matcher.quoteReplacement("distributionSha256Sum = '" + sha256 + "'")))
        .orElse(content);
  }

  /** Never downgrades: a declared version that is the same or newer is kept. */
  private static boolean isNewer(String content, String version) {
    Matcher current = CURRENT_VERSION.matcher(content);
    return !current.find() || UpdateUtils.isNewerVersion(current.group(1), version);
  }

  private static String replaceVersion(String content, String version) {
    return Utils.replaceExpression(
        content,
        "gradleVersion\\s*=\\s*.*",
        Matcher.quoteReplacement("gradleVersion = '" + version + "'"));
  }
}
