package co.com.bancolombia.analytics;

import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.RestConsumer;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyticsExporter {
  private static final String ENDPOINT = "";

  public static void collectMetric(AnalyticsBody body) {
    if (!shouldMock()) {
      try {
        RestConsumer.postRequest(ENDPOINT, body, Map.class);
      } catch (Exception e) {
        e.printStackTrace();
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
