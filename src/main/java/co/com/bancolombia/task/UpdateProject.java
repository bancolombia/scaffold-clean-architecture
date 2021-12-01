package co.com.bancolombia.task;

import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public class UpdateProject extends CleanArchitectureDefaultTask {
  public static final String PLUGIN_RELEASES =
      "https://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
  private List<String> dependencies = new LinkedList<>();

  @Option(option = "dependencies", description = "Set dependencies to update")
  public void setName(List dependencies) {
    this.dependencies.addAll(dependencies);
  }

  @TaskAction
  public void updateProject() throws IOException {
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle(
        "Dependencies to update: {}",
        (dependencies.isEmpty() ? "all" : dependencies.stream().collect(Collectors.joining())));

    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectMapper = instantiateMapper();
    Request request = new Request.Builder().url(PLUGIN_RELEASES).build();
    ResponseBody response = client.newCall(request).execute().body();
    Release[] releases = objectMapper.readValue(response.string(), Release[].class);
    logger.lifecycle("Latest version: {}", releases[0].getTagName());
    updatePlugin(releases[0].getTagName());

    builder.persist();
  }

  private void updatePlugin(String lastRelease) throws IOException {
    if (lastRelease.equals(Utils.getVersionPlugin())) {
      logger.lifecycle("You are already using the latest version of the plugin");
      return;
    }
    if (builder.isKotlin()) {
      builder.updateProperty(
          "build.gradle.kts",
          "cleanArchitecture",
          "  id(\"co.com.bancolombia.cleanArchitecture\") version \""
              + lastRelease
              + "\" + releases[0].getTagName() + \"");
    } else {
      builder.updateProperty(
          "gradle.properties", "systemProp.version", "systemProp.version=" + lastRelease);
      builder.updateProperty(
          "build.gradle",
          "cleanArchitectureVersion =",
          "\t\tcleanArchitectureVersion = " + "'" + lastRelease + "'");
    }
    logger.lifecycle("Plugin Updated");
  }

  private ObjectMapper instantiateMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}
