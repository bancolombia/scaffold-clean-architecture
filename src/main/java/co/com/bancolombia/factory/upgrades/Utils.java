package co.com.bancolombia.factory.upgrades;

import co.com.bancolombia.factory.ModuleBuilder;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

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
}
