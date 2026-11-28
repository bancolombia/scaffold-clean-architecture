package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.DEPLOYMENT_DIR;
import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE_NAME;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds the OpenTelemetry agent to the Dockerfile of the projects that enabled it with {@code
 * cleanPlugin.otel}. The agent is generated in build/libs next to the jar, so the Docker build
 * context has both: the jar is copied by its name instead of a wildcard, and Dockerfiles that copy
 * the agent from the former otel folder are moved to the new location. The agent is copied next to
 * the jar, so its path depends on the WORKDIR: /app/ in the current template and / in the
 * Dockerfiles that do not define one.
 */
public class UpgradeY2026M11D07DockerOtelAgent implements UpgradeAction {
  private static final String AGENT = "opentelemetry-javaagent.jar";
  private static final String LEGACY_AGENT_SOURCE = "COPY otel/" + AGENT + " ";
  private static final Pattern OTEL_ENABLED = Pattern.compile("(?m)^\\s*otel\\s*=\\s*true\\b");
  private static final Pattern WORKDIR = Pattern.compile("(?m)^\\s*WORKDIR\\s+(/\\S*)\\s*$");
  private static final Pattern COPY_JAR =
      Pattern.compile("(?m)^(COPY\\s+(\\S+\\.jar)\\s+\\S+)[ \\t]*$");
  private static final Pattern JAVA_OPTS = Pattern.compile("(ENV\\s+JAVA_OPTS\\s*=\\s*\")\\s*");

  @Override
  public boolean up(ModuleBuilder builder) {
    try {
      if (!OTEL_ENABLED.matcher(builder.readFile(BUILD_GRADLE)).find()) {
        return false;
      }
      String jar = builder.getProjectName() + ".jar";
      boolean applied = false;
      for (String dockerfile : builder.findFiles(DEPLOYMENT_DIR, DOCKERFILE_NAME)) {
        applied |= update(builder, dockerfile, jar);
      }
      return applied;
    } catch (IOException e) {
      builder
          .getLogger()
          .debug("build.gradle or Dockerfile not found, skipping: {}", e.getMessage());
      return false;
    }
  }

  private String upgrade(String content, String jar) {
    if (content.contains(LEGACY_AGENT_SOURCE)) {
      return copyOnlyTheJar(content.replace(LEGACY_AGENT_SOURCE, "COPY " + AGENT + " "), jar);
    }
    Matcher copyJar = COPY_JAR.matcher(content);
    if (content.contains("javaagent") || !copyJar.find() || !JAVA_OPTS.matcher(content).find()) {
      return content;
    }
    String agentPath = workdir(content) + AGENT;
    String withCopy =
        content.substring(0, copyJar.end(1))
            + "\nCOPY "
            + AGENT
            + " "
            + agentPath
            + content.substring(copyJar.end(1));
    return copyOnlyTheJar(
        JAVA_OPTS
            .matcher(withCopy)
            .replaceFirst("$1" + Matcher.quoteReplacement("-javaagent:" + agentPath + " ")),
        jar);
  }

  /**
   * The agent is in the same folder as the jar, so a wildcard would match both: the first jar COPY
   * gets the name of the jar that bootJar generates.
   */
  private String copyOnlyTheJar(String content, String jar) {
    Matcher copyJar = COPY_JAR.matcher(content);
    if (!copyJar.find() || !copyJar.group(2).contains("*")) {
      return content;
    }
    String source = copyJar.group(2);
    return content.substring(0, copyJar.start(2))
        + source.substring(0, source.lastIndexOf('/') + 1)
        + jar
        + content.substring(copyJar.end(2));
  }

  /** The last absolute WORKDIR with a trailing slash, or / when the image does not define one. */
  private String workdir(String content) {
    Matcher matcher = WORKDIR.matcher(content);
    String workdir = "/";
    while (matcher.find()) {
      workdir = matcher.group(1);
    }
    return workdir.endsWith("/") ? workdir : workdir + "/";
  }

  @Override
  public String name() {
    return "Dockerfile OpenTelemetry agent";
  }

  @Override
  public String description() {
    return "Add the OpenTelemetry agent to the Dockerfile when cleanPlugin.otel is enabled";
  }

  /** A Dockerfile that can't be read is skipped, so the others are still updated. */
  private boolean update(ModuleBuilder builder, String dockerfile, String jar) {
    try {
      return builder.updateFile(dockerfile, content -> upgrade(content, jar));
    } catch (IOException e) {
      builder.getLogger().debug("Can't read {}, skipping: {}", dockerfile, e.getMessage());
      return false;
    }
  }
}
