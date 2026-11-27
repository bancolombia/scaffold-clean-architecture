package co.com.bancolombia.utils.operations.http;

import java.io.IOException;
import lombok.Getter;

/** A response with a status code other than 2xx, keeping the code to tell apart 404 from others. */
@Getter
public class HttpStatusException extends IOException {
  private final int statusCode;
  private final String url;

  public HttpStatusException(int statusCode, String url) {
    super("HTTP " + statusCode + " for " + url);
    this.statusCode = statusCode;
    this.url = url;
  }

  public boolean isNotFound() {
    return statusCode == 404;
  }
}
