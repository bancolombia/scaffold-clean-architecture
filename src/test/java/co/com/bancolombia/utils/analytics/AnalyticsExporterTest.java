package co.com.bancolombia.utils.analytics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import co.com.bancolombia.models.AnalyticsBody;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class AnalyticsExporterTest {

  @Test
  void shouldNotSendMetricWhenEndpointIsNotConfigured() throws Exception {
    // Arrange: sources are built with an empty endpoint and no simulated rest
    try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class);
        MockedStatic<RestConsumer> rest = mockStatic(RestConsumer.class)) {
      fileUtils.when(() -> FileUtils.readProperties(anyString(), anyString())).thenReturn("false");
      // Act
      AnalyticsExporter.collectMetric(AnalyticsBody.defaults());
      // Assert
      rest.verify(() -> RestConsumer.postRequest(anyString(), any(), any()), Mockito.never());
    }
  }
}
