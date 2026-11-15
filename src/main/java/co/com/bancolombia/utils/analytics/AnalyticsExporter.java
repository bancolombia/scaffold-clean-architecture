package co.com.bancolombia.utils.analytics;

import co.com.bancolombia.models.AnalyticsBody;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyticsExporter {
  private static final String ENDPOINT = "";

  // ENDPOINT is empty in the source and replaced on release, so the IDE sees a constant condition
  @SuppressWarnings("ConstantValue")
  public static void collectMetric(AnalyticsBody body) throws IOException {
    if (ENDPOINT.isBlank() || shouldMock()) {
      return;
    }
    RestConsumer.postRequest(ENDPOINT, body, Map.class);
  }

  private static boolean shouldMock() {
    try {
      return "true".equals(FileUtils.readProperties(".", "simulateRest"));
    } catch (IOException ignored) {
      return false;
    }
  }
}
