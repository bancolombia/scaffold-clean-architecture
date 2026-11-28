package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spring Boot 4 moved the auto-configured WebClient.Builder and RestClient.Builder to their own
 * starters, and Resilience4j has a module for it:
 *
 * <ul>
 *   <li>In every module, resilience4j-spring-boot3 becomes resilience4j-spring-boot4, at least in
 *       the version that the plugin generates.
 *   <li>In the rest consumer and the Cognito token provider, which are clients, the webflux and
 *       webmvc starters and their test starters become the webclient and restclient ones. A web
 *       server belongs to an entry point or to app-service, so it is not kept in these adapters.
 *   <li>When one of these adapters was the only module that declared the server starter, it is
 *       added to app-service, so the application keeps its web server, for example for Actuator.
 * </ul>
 */
public class UpgradeY2026M11D27RestClientsSpringBoot4 implements UpgradeAction {
  private static final String BOOT = "org.springframework.boot:";
  private static final List<String> CLIENT_MODULES =
      List.of(
          "infrastructure/driven-adapters/rest-consumer/build.gradle",
          "infrastructure/driven-adapters/cognito-token-provider/build.gradle");
  private static final Map<String, String> CLIENT_STARTERS =
      Map.of(
          "spring-boot-starter-webflux", "spring-boot-starter-webclient",
          "spring-boot-starter-webflux-test", "spring-boot-starter-webclient-test",
          "spring-boot-starter-webmvc", "spring-boot-starter-restclient",
          "spring-boot-starter-webmvc-test", "spring-boot-starter-restclient-test");
  private static final List<String> SERVER_STARTERS =
      List.of("spring-boot-starter-webflux", "spring-boot-starter-webmvc");
  private static final Pattern DEPENDENCIES_BLOCK =
      Pattern.compile("(?m)^dependencies\\s*\\{[ \\t]*$");
  private static final String RESILIENCE4J = "io.github.resilience4j:";
  private static final String RESILIENCE4J_BOOT3 = "resilience4j-spring-boot3";
  // The version is optional: it can come from the BOM, or from a variable that is kept
  private static final Pattern RESILIENCE4J_BOOT3_DECLARATION =
      Pattern.compile(
          Pattern.quote(RESILIENCE4J + RESILIENCE4J_BOOT3) + "(?::([^'\"\\s]+))?(?=['\"\\s)])");
  private static final Pattern RESILIENCE4J_LITERAL_VERSION =
      Pattern.compile(Pattern.quote(RESILIENCE4J) + "([\\w-]+):(\\d[^'\"\\s]*)(?=['\"])");
  private static final Pattern MAIN_CONFIGURATION =
      Pattern.compile(
          "(?m)^[ \\t]*(implementation|api|runtimeOnly)\\s*\\(?\\s*['\"]([^'\"]+)['\"]");

  @Override
  public boolean up(ModuleBuilder builder) {
    List<String> files;
    try {
      files = Utils.getAllFilesWithGradleExtension(builder.getProjectDir().getPath());
    } catch (IOException e) {
      builder.getLogger().debug("Can't list the Gradle files, skipping: {}", e.getMessage());
      return false;
    }
    List<String> serversToKeep = serversOnlyInClients(builder, files);
    boolean applied = false;
    for (String file : files) {
      applied |= update(builder, file);
    }
    for (String server : serversToKeep) {
      applied |= addToAppService(builder, server);
    }
    return applied;
  }

  /** The server starters that only the client adapters declare, which would be lost otherwise. */
  private static List<String> serversOnlyInClients(ModuleBuilder builder, List<String> files) {
    return SERVER_STARTERS.stream()
        .filter(
            server ->
                files.stream()
                    .filter(UpgradeY2026M11D27RestClientsSpringBoot4::isClientModule)
                    .anyMatch(file -> declares(builder, file, server)))
        .filter(
            server ->
                files.stream()
                    .filter(file -> !isClientModule(file))
                    .noneMatch(file -> declares(builder, file, server)))
        .toList();
  }

  /**
   * Whether the file declares the starter for the application: implementation, api or runtimeOnly.
   * A test configuration or a comment does not start a web server.
   */
  private static boolean declares(ModuleBuilder builder, String file, String starter) {
    try {
      return declaresForMain(builder.readFile(file), starter);
    } catch (IOException e) {
      return false;
    }
  }

  static boolean declaresForMain(String content, String starter) {
    return MAIN_CONFIGURATION
        .matcher(content)
        .results()
        .anyMatch(match -> match.group(2).equals(BOOT + starter));
  }

  private boolean addToAppService(ModuleBuilder builder, String server) {
    String line = "    implementation '" + BOOT + server + "'";
    try {
      boolean added =
          builder.updateFile(
              APP_BUILD_GRADLE,
              content -> {
                if (declaresForMain(content, server)) {
                  return content;
                }
                Matcher block = DEPENDENCIES_BLOCK.matcher(content);
                return block.find()
                    ? content.substring(0, block.end())
                        + "\n"
                        + line
                        + content.substring(block.end())
                    : content + "\ndependencies {\n" + line + "\n}\n";
              });
      if (added) {
        builder
            .getLogger()
            .lifecycle(
                "{} moved to app-service, the rest client adapters no longer declare it", server);
      }
      return added;
    } catch (IOException e) {
      builder
          .getLogger()
          .warn(
              "Add {} to the module that starts the web server: the rest client adapters no longer"
                  + " declare it",
              server);
      return false;
    }
  }

  private boolean update(ModuleBuilder builder, String file) {
    boolean client = isClientModule(file);
    try {
      return builder.updateFile(
          file,
          content -> {
            String updated = useResilience4jBoot4(content);
            return client ? useClientStarters(updated) : updated;
          });
    } catch (IOException e) {
      builder.getLogger().debug("Can't read {}, skipping: {}", file, e.getMessage());
      return false;
    }
  }

  private static boolean isClientModule(String file) {
    String path = file.replace('\\', '/');
    return CLIENT_MODULES.stream().anyMatch(path::endsWith);
  }

  /**
   * Renames resilience4j-spring-boot3 to resilience4j-spring-boot4. A literal version older than
   * the one of the plugin is raised to it, and so are the other Resilience4j modules of the file,
   * so they keep the same version; a version from the BOM or a variable is kept as is.
   */
  String useResilience4jBoot4(String content) {
    boolean[] raised = {false};
    String renamed =
        RESILIENCE4J_BOOT3_DECLARATION
            .matcher(content)
            .replaceAll(
                match -> {
                  String version = match.group(1);
                  String suffix = version == null ? "" : ":" + version;
                  if (version != null
                      && !version.startsWith("$")
                      && UpdateUtils.isNewerVersion(version, Constants.RESILIENCE_4J_VERSION)) {
                    suffix = ":" + Constants.RESILIENCE_4J_VERSION;
                    raised[0] = true;
                  }
                  return Matcher.quoteReplacement(
                      RESILIENCE4J + "resilience4j-spring-boot4" + suffix);
                });
    return raised[0] ? alignResilience4j(renamed) : renamed;
  }

  private static String alignResilience4j(String content) {
    return RESILIENCE4J_LITERAL_VERSION
        .matcher(content)
        .replaceAll(
            match ->
                Matcher.quoteReplacement(
                    UpdateUtils.isNewerVersion(match.group(2), Constants.RESILIENCE_4J_VERSION)
                        ? RESILIENCE4J + match.group(1) + ":" + Constants.RESILIENCE_4J_VERSION
                        : match.group()));
  }

  /**
   * Each server starter becomes the client one. When the client one is already declared, the line
   * of the server starter is removed, so it is not declared twice.
   */
  String useClientStarters(String content) {
    String updated = content;
    for (Map.Entry<String, String> starter : CLIENT_STARTERS.entrySet()) {
      Pattern server = declaration(starter.getKey());
      if (declaration(starter.getValue()).matcher(updated).find()) {
        updated =
            Pattern.compile("(?m)^.*" + server.pattern() + ".*\\R?")
                .matcher(updated)
                .replaceAll("");
      } else {
        updated =
            server
                .matcher(updated)
                .replaceAll(
                    match ->
                        Matcher.quoteReplacement(
                            match.group(1) + BOOT + starter.getValue() + match.group(2)));
      }
    }
    return updated;
  }

  /**
   * The starter by its whole name: spring-boot-starter-webflux is not
   * spring-boot-starter-webflux-test.
   */
  private static Pattern declaration(String starter) {
    return Pattern.compile("(['\"])" + Pattern.quote(BOOT + starter) + "(['\"])");
  }

  @Override
  public String name() {
    return "Rest clients for Spring Boot 4";
  }

  @Override
  public String description() {
    return "Use Resilience4j for Spring Boot 4, and the webclient and restclient starters in the"
        + " rest consumer and the Cognito token provider";
  }
}
