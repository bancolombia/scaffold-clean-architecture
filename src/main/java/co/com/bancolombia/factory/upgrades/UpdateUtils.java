package co.com.bancolombia.factory.upgrades;

import co.com.bancolombia.factory.ModuleBuilder;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUtils {

    public static boolean appendIfNotContains(
            ModuleBuilder builder, String file, String contains, String toAdd) throws IOException {
        AtomicBoolean applied = new AtomicBoolean(false);
        builder.updateFile(
                file,
                content -> {
                    if (!content.contains(contains)) {
                        applied.getAndSet(true);
                        return content + toAdd;
                    }
                    return content;
                });
        return applied.get();
    }
    public static boolean contains(
            ModuleBuilder builder, String file, String contains) throws IOException {
        AtomicBoolean applied = new AtomicBoolean(true);
        builder.updateFile(
                file,
                content -> {
                    if (!content.contains(contains)) {
                         applied.getAndSet(false);
                    }
                    return content;
                });
        return applied.get();
    }

  public static void updateVersions(
      ModuleBuilder builder, String file, String property, String version) throws IOException {
    builder.updateExpression(file, "(" + property + "\\s?=\\s?)'.+'", "$1'" + version + "'");
    builder.updateExpression(file, "(" + property + "\\s?=\\s?)\".+\"", "$1'" + version + "'");
  }

  public static void updateConfiguration(
      ModuleBuilder builder, String file, String configuration, String newConfiguration)
      throws IOException {
    builder.updateExpression(file, "(" + configuration + "\\s)", newConfiguration + " ");
    builder.updateExpression(file, "(" + configuration + "\\()", newConfiguration + "(");
  }
}
