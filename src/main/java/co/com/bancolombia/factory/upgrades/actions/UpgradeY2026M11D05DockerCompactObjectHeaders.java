package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.util.regex.Pattern;

/** Adds -XX:+UseCompactObjectHeaders to the Dockerfile JAVA_OPTS of projects running Java 25. */
public class UpgradeY2026M11D05DockerCompactObjectHeaders implements UpgradeAction {
  private static final String FLAG = "-XX:+UseCompactObjectHeaders";
  private static final Pattern JAVA_25_IMAGE = Pattern.compile("(?m)^\\s*FROM\\s+\\S*:25\\b");
  private static final Pattern JAVA_OPTS = Pattern.compile("(ENV\\s+JAVA_OPTS\\s*=\\s*\")\\s*");

  @Override
  public boolean up(ModuleBuilder builder) {
    try {
      return builder.updateFile(DOCKERFILE, this::addFlag);
    } catch (IOException e) {
      builder.getLogger().debug("Dockerfile not found, skipping: {}", e.getMessage());
      return false;
    }
  }

  private String addFlag(String content) {
    if (content.contains(FLAG) || !JAVA_25_IMAGE.matcher(content).find()) {
      return content;
    }
    return JAVA_OPTS.matcher(content).replaceFirst("$1" + FLAG + " ");
  }

  @Override
  public String name() {
    return "Dockerfile compact object headers";
  }

  @Override
  public String description() {
    return "Add -XX:+UseCompactObjectHeaders to JAVA_OPTS in the Dockerfile when using Java 25";
  }
}
