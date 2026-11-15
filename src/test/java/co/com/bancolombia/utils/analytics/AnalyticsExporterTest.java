package co.com.bancolombia.utils.analytics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import co.com.bancolombia.models.AnalyticsBody;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class AnalyticsExporterTest {
  private static final String URL = "https://analytics.example.com";

  @Test
  void shouldNotSendMetricWhenEndpointIsNotConfigured() throws Exception {
    // Arrange: sources are built with an empty endpoint and no simulated rest
    try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class);
        MockedStatic<RestConsumer> rest = mockStatic(RestConsumer.class)) {
      fileUtils.when(() -> FileUtils.readProperties(anyString(), anyString())).thenReturn("false");
      // Act
      AnalyticsExporter.collectMetric(AnalyticsBody.defaults());
      // Assert
      rest.verify(() -> RestConsumer.postRequest(anyString(), any(), any()), never());
    }
  }

  @Test
  void shouldNotSendMetricWhenRestIsSimulated() throws Exception {
    try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class);
        MockedStatic<RestConsumer> rest = mockStatic(RestConsumer.class)) {
      fileUtils.when(() -> FileUtils.readProperties(anyString(), anyString())).thenReturn("true");
      // Act
      AnalyticsExporter.collectMetric(URL, AnalyticsBody.defaults());
      // Assert
      rest.verify(() -> RestConsumer.postRequest(anyString(), any(), any()), never());
    }
  }

  @Test
  void shouldSendMetricWhenEndpointIsConfigured() throws Exception {
    AnalyticsBody body = AnalyticsBody.defaults();
    try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class);
        MockedStatic<RestConsumer> rest = mockStatic(RestConsumer.class)) {
      fileUtils.when(() -> FileUtils.readProperties(anyString(), anyString())).thenReturn("false");
      // Act
      AnalyticsExporter.collectMetric(URL, body);
      // Assert
      rest.verify(() -> RestConsumer.postRequest(URL, body, Map.class), times(1));
    }
  }

  @Test
  void shouldSendMetricWhenSimulateRestPropertyCannotBeRead() throws Exception {
    AnalyticsBody body = AnalyticsBody.defaults();
    try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class);
        MockedStatic<RestConsumer> rest = mockStatic(RestConsumer.class)) {
      fileUtils
          .when(() -> FileUtils.readProperties(anyString(), anyString()))
          .thenThrow(new IOException("missing"));
      // Act
      AnalyticsExporter.collectMetric(URL, body);
      // Assert
      rest.verify(() -> RestConsumer.postRequest(URL, body, Map.class), times(1));
    }
  }
}
