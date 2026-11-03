package co.com.bancolombia.factory.upgrades;

import co.com.bancolombia.exceptions.InvalidStateException;
import co.com.bancolombia.factory.ModuleBuilder;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUtils {

  /** Returns the balanced {@code marker { ... }} block (brace-aware), or null if not found. */
  public static String extractBlock(String content, String marker) {
    int markerIndex = content.indexOf(marker);
    if (markerIndex == -1) {
      return null;
    }
    int braceStart = content.indexOf('{', markerIndex);
    if (braceStart == -1) {
      return null;
    }
    int depth = 0;
    int i = braceStart;
    for (; i < content.length(); i++) {
      char c = content.charAt(i);
      if (c == '{') {
        depth++;
      } else if (c == '}') {
        depth--;
        if (depth == 0) {
          break;
        }
      }
    }
    if (depth != 0) {
      return null;
    }
    return content.substring(markerIndex, i + 1);
  }

  public static boolean appendIfNotContains(
      ModuleBuilder builder, String file, String contains, String toAdd) throws IOException {
    return builder.updateFile(
        file,
        content -> {
          if (!content.contains(contains)) {
            return content + toAdd;
          }
          return content;
        });
  }

  public static boolean updateVersions(
      ModuleBuilder builder, String file, String property, String version) throws IOException {
    boolean appliedA =
        builder.updateExpression(file, "(" + property + "\\s?=\\s?)'.+'", "$1'" + version + "'");
    boolean appliedB =
        builder.updateExpression(file, "(" + property + "\\s?=\\s?)\".+\"", "$1'" + version + "'");
    return appliedA || appliedB;
  }

  public static boolean updateConfiguration(
      ModuleBuilder builder, String file, String configuration, String newConfiguration)
      throws IOException {
    boolean appliedA =
        builder.updateExpression(file, "(" + configuration + "\\s)", newConfiguration + " ");
    boolean appliedB =
        builder.updateExpression(file, "(" + configuration + "\\()", newConfiguration + "(");
    return appliedA || appliedB;
  }

  /** Inserts concatValue before match if main does not containsValue */
  public static String insertBeforeMatch(
      String main, String match, String containsValue, String concatValue) {
    if (main.contains(containsValue)) {
      return main;
    }
    int start = main.indexOf(match);
    if (start == -1) {
      throw new InvalidStateException("Match " + match + "was not found");
    }
    return main.substring(0, start) + concatValue + main.substring(start);
  }

  public static String insertAfterMatch(
      String main, String match, String containsValue, String concatValue) {
    if (main.contains(containsValue)) {
      return main;
    }
    int start = main.indexOf(match);
    if (start == -1) {
      throw new InvalidStateException("Match " + match + "was not found");
    }
    start = start + match.length();
    return main.substring(0, start) + concatValue + main.substring(start);
  }

  public static String addToStartIfNotContains(
      String main, String containsValue, String concatValue) {
    if (main.contains(containsValue)) {
      return main;
    }
    return concatValue + main;
  }

  public static String replace(String content, String previous, String next) {
    return content.replace(previous, next);
  }

  public static boolean isNewerVersion(String current, String newer) {
    if (newer == null) {
      return false;
    }
    if (current == null) {
      return true;
    }
    if (current.equals(newer)) {
      return false;
    }
    String[] v1Parts = current.split("\\.");
    String[] v2Parts = newer.split("\\.");

    int length = Math.max(v1Parts.length, v2Parts.length);

    for (int i = 0; i < length; i++) {
      // Get the current part of each version, default to "0" if shorter
      int part1 = i < v1Parts.length ? asInteger(v1Parts[i]) : 0;
      int part2 = i < v2Parts.length ? asInteger(v2Parts[i]) : 0;

      if (part1 < part2) {
        return true;
      }
      if (part1 > part2) {
        return false;
      }
    }

    return false;
  }

  public static boolean oneOfAll(boolean... values) {
    for (boolean value : values) {
      if (value) {
        return true;
      }
    }
    return false;
  }

  private static int asInteger(String part) {
    try {
      return Integer.parseInt(part);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
