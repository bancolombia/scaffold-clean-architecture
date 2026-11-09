package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds the OpenTelemetry agent to the Dockerfile of the projects that enabled it with {@code
 * cleanPlugin.otel}. The agent is copied next to the jar, so its path depends on the WORKDIR: /app/
 * in the current template and / in the Dockerfiles that do not define one.
 */
public class UpgradeY2026M11D07DockerOtelAgent implements UpgradeAction {
  private static final String AGENT = "opentelemetry-javaagent.jar";
  private static final Pattern OTEL_ENABLED = Pattern.compile("(?m)^\\s*otel\\s*=\\s*true\\b");
  private static final Pattern WORKDIR = Pattern.compile("(?m)^\\s*WORKDIR\\s+(/\\S*)\\s*$");
  private static final Pattern COPY_JAR =
      Pattern.compile("(?m)^(COPY\\s+\\S+\\.jar\\s+\\S+)[ \\t]*$");
  private static final Pattern JAVA_OPTS = Pattern.compile("(ENV\\s+JAVA_OPTS\\s*=\\s*\")\\s*");

  @Override
  public boolean up(ModuleBuilder builder) {
    try {
      if (!OTEL_ENABLED.matcher(builder.readFile(BUILD_GRADLE)).find()) {
        return false;
      }
      return builder.updateFile(DOCKERFILE, this::addAgent);
    } catch (IOException e) {
      builder
          .getLogger()
          .debug("build.gradle or Dockerfile not found, skipping: {}", e.getMessage());
      return false;
    }
  }

  private String addAgent(String content) {
    Matcher copyJar = COPY_JAR.matcher(content);
    if (content.contains("javaagent") || !copyJar.find() || !JAVA_OPTS.matcher(content).find()) {
      return content;
    }
    String agentPath = workdir(content) + AGENT;
    String withCopy =
        content.substring(0, copyJar.end(1))
            + "\nCOPY otel/"
            + AGENT
            + " "
            + agentPath
            + content.substring(copyJar.end(1));
    return JAVA_OPTS
        .matcher(withCopy)
        .replaceFirst("$1" + Matcher.quoteReplacement("-javaagent:" + agentPath + " "));
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
}
