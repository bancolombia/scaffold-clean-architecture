package co.com.bancolombia.analytics;

import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.RestConsumer;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gradle.api.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyticsExporter {
  private static final String ENDPOINT = "";

  public static void collectMetric(AnalyticsBody body, Logger logger) {
    if (!shouldMock()) {
      try {
        RestConsumer.postRequest(ENDPOINT, body, Map.class);
      } catch (Exception e) {
        logger.warn("Error sending analytics", e);
      }
    }
  }

  private static boolean shouldMock() {
    try {
      return "true".equals(FileUtils.readProperties(".", "simulateRest"));
    } catch (IOException ignored) {
      return false;
    }
  }
}
