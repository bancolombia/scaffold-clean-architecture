package co.com.bancolombia.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class DependencyReleasesDeserializer extends StdDeserializer<DependencyRelease> {

  public DependencyReleasesDeserializer() {
    this(null);
  }

  public DependencyReleasesDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public DependencyRelease deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {

    JsonNode productNode = jp.getCodec().readTree(jp);
    DependencyRelease dependencyRelease = new DependencyRelease();
    JsonNode list = productNode.get("response").get("docs");
    int i = 0;
    while (list.has(i)) {
      JsonNode dependency = list.get(i);
      String version = dependency.get("v").textValue();
      if (isStable(version)) {
        dependencyRelease.setGroup(dependency.get("g").textValue());
        dependencyRelease.setArtifact(dependency.get("a").textValue());
        dependencyRelease.setVersion(version);
        break;
      }
      i++;
    }
    return dependencyRelease;
  }

  private boolean isStable(String version) {
    return !version.contains("alpha") && !version.contains("beta") && !version.contains("RC");
  }
}
