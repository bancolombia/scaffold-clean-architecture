package co.com.bancolombia.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/** This class should translate the dependency check vulnerabilities to sonar issues */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SonarCheck {
  public static final String INPUT = "build/reports/dependency-check-report.json";
  public static final String OUTPUT = "build/reports/dependency-check-sonar.json";
  public static final String DEFAULT_LOCATION =
      "src/main/java/co/com/bancolombia/MainApplication.java";

  @SneakyThrows
  public static void parse(Set<String> subProjectPath) {
    final ObjectMapper mapper =
        JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    subProjectPath.forEach(p -> parseSingle(p, mapper));
  }

  @SneakyThrows
  private static void parseSingle(String projectPath, ObjectMapper mapper) {
    List<Issue> issues;
    File inputFile = new File(projectPath, INPUT);
    if (inputFile.exists()) {
      issues =
          Stream.of(Files.readString(inputFile.toPath()))
              .map(content -> getReport(content, mapper))
              .flatMap(report -> report.dependencies.stream())
              .filter(
                  dependency ->
                      dependency.getVulnerabilities() != null
                          && !dependency.getVulnerabilities().isEmpty())
              .flatMap(SonarCheck::extractIssues)
              .toList();
    } else {
      issues = new ArrayList<>();
    }

    File outputDir = new File(projectPath, "build/reports/");
    outputDir.mkdirs();
    File outputFile = new File(outputDir, "dependency-check-sonar.json");
    Files.writeString(
        outputFile.toPath(),
        mapper.writeValueAsString(SonarReport.builder().issues(issues).build()));
  }

  private static Stream<Issue> extractIssues(Dependency dependency) {
    return dependency.getVulnerabilities().stream()
        .map(
            vulnerability ->
                Issue.builder()
                    .severity(parseSeverity(vulnerability.getSeverity()))
                    .primaryLocation(
                        IssueLocation.builder()
                            .message(parseMessage(dependency, vulnerability))
                            .build())
                    .build());
  }

  private static String parseMessage(Dependency dependency, Vulnerability vulnerability) {
    return vulnerability.getName()
        + " `"
        + resolveDependencyName(dependency)
        + "` "
        + vulnerability.getDescription();
  }

  private static String resolveDependencyName(Dependency dependency) {
    if (dependency.packages.isEmpty()) {
      return dependency.fileName;
    }
    String id = dependency.getPackages().get(0).getId();
    return id != null ? id.replace("pkg:maven/", "").replace('/', ':').replace('@', ':') : "";
  }

  private static String parseSeverity(String severity) {
    return switch (severity) {
      case "LOW" -> "MINOR";
      case "MEDIUM" -> "MAJOR";
      case "HIGH" -> "CRITICAL";
      case "CRITICAL" -> "BLOCKER";
      default -> "INFO";
    };
  }

  @SneakyThrows
  private static Report getReport(String json, ObjectMapper mapper) {
    return mapper.readValue(json, Report.class);
  }

  // Input classes
  @Setter
  @Getter
  @NoArgsConstructor
  public static class Report {
    private List<Dependency> dependencies;
  }

  @Setter
  @Getter
  @NoArgsConstructor
  public static class Dependency {
    private String fileName;
    private List<Vulnerability> vulnerabilities;
    private List<Package> packages;
  }

  @Setter
  @Getter
  @NoArgsConstructor
  public static class Vulnerability {
    private String name;
    private String severity;
    private String description;
  }

  @Setter
  @Getter
  @NoArgsConstructor
  public static class Package {
    private String id;
  }

  // Output classes
  @Getter
  @Builder
  public static class SonarReport {
    private final List<Issue> issues;
  }

  @Getter
  @Builder
  public static class Issue {
    @Builder.Default private final String engineId = "Dependency-Check";
    @Builder.Default private final String ruleId = "vulnerable-dependency";
    @Builder.Default private final String type = "VULNERABILITY";
    private final String severity;
    private final IssueLocation primaryLocation;
  }

  @Getter
  @Builder
  public static class IssueLocation {
    private final String message;
    @Builder.Default private final String filePath = DEFAULT_LOCATION;
    @Builder.Default private final TextRange textRange = TextRange.builder().build();
  }

  @Getter
  @Builder
  public static class TextRange {
    @Builder.Default private final int startLine = 7;
    @Builder.Default private final int endLine = 7;
    @Builder.Default private final int startColumn = 13;
    @Builder.Default private final int endColumn = 28;
  }
}
