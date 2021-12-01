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
    dependencyRelease.setGroup(productNode.get("response").get("docs").get(0).get("g").textValue());
    dependencyRelease.setArtifact(
        productNode.get("response").get("docs").get(0).get("a").textValue());
    dependencyRelease.setVersion(
        productNode.get("response").get("docs").get(0).get("v").textValue());
    return dependencyRelease;
  }
}
