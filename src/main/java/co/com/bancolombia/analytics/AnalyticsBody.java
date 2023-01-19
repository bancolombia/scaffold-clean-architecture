package co.com.bancolombia.analytics;

import co.com.bancolombia.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import lombok.*;

@Getter
@Builder
public class AnalyticsBody {
  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("timestamp_micros")
  private long timestampMicros;

  @JsonProperty("user_properties")
  private UserProperties userProperties;

  @Builder.Default private List<Event> events = new ArrayList<>();

  public static AnalyticsBody defaults() {
    return AnalyticsBody.builder()
        .clientId(System.getProperty("user.name"))
        .timestampMicros(System.currentTimeMillis() * 1000)
        .userProperties(UserProperties.instance())
        .build();
  }

  public AnalyticsBody withEvent(Event event) {
    this.events.add(event);
    return this;
  }

  public static class UserProperties extends HashMap<String, UserProperties.Val> {
    public UserProperties() {
      super();
      put("plugin_version", new Val(Constants.PLUGIN_VERSION));
      put("country", new Val(Locale.getDefault().getDisplayCountry()));
      put("language", new Val(Locale.getDefault().getLanguage()));
      put("java_specification", new Val(System.getProperty("java.specification.version")));
      loadProperties(
          "java.vendor",
          "java.vendor.version",
          "java.version",
          "java.runtime.name",
          "java.runtime.version");
    }

    private void loadProperties(String... property) {
      Arrays.stream(property)
          .forEach(prop -> put(prop.replace('.', '_'), new Val(System.getProperty(prop))));
    }

    public static UserProperties instance() {
      return new UserProperties();
    }

    public UserProperties with(String key, Object value) {
      put(key, new Val(value));
      return this;
    }

    @Getter
    @RequiredArgsConstructor
    private static class Val {
      private final Object value;
    }
  }

  @Getter
  @RequiredArgsConstructor
  public static class Event {
    private final String name;
    private Params params;

    public static Event withName(String name) {
      return new Event(name);
    }

    public Event withParams(Params params) {
      this.params = params;
      return this;
    }

    public static class Params extends HashMap<String, Object> {
      public Params() {
        super();
      }

      public static Params empty() {
        return new Params()
            .with("operating_system", System.getProperty("os.name"))
            .with("operating_system_version", System.getProperty("os.version"));
      }

      public Params with(String key, Object value) {
        put(key, value);
        return this;
      }
    }
  }
}
