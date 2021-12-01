package co.com.bancolombia.task;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public class UpdateProjectTask extends CleanArchitectureDefaultTask {
  public static final String PLUGIN_RELEASES =
      "https://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
  public static final String DEPENDENCY_RELEASES =
      "https://search.maven.org/solrsearch/select?q=g:%22%s%22+AND+a:%22%s%22&core=gav&rows=1&wt=json";
  private List<String> dependencies = new LinkedList<>();
  ObjectMapper objectMapper = instantiateMapper();
  OkHttpClient client = new OkHttpClient();

  @Option(option = "dependencies", description = "Set dependencies to update")
  public void setName(String dependencies) {
    this.dependencies.addAll(Arrays.asList(dependencies.split("[ ,]+")));
  }

  @TaskAction
  public void updateProject() throws IOException {
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle(
        "Dependencies to update: {}",
        (dependencies.isEmpty() ? "all" : dependencies.stream().collect(Collectors.joining(", "))));

    Release[] releases = getPluginReleases();
    updatePlugin(releases[0].getTagName());
    updateDependencies();

    builder.persist();
  }

  private void updateDependencies() throws IOException {
    logger.lifecycle("Updating Dependencies ");

    if (dependencies.isEmpty()) {

    } else {
      for (String dependency : dependencies) {
        getDependencyReleases(dependency);
      }
    }
  }

  private Release[] getPluginReleases() throws IOException {
    Request request = new Request.Builder().url(PLUGIN_RELEASES).build();
    ResponseBody response = client.newCall(request).execute().body();
    Release[] releases = objectMapper.readValue(response.string(), Release[].class);
    logger.lifecycle("Latest version: {}", releases[0].getTagName());
    return releases;
  }

  private void getDependencyReleases(String dependency) throws IOException {
    String[] id = dependency.split(":");
    if (id.length == 2) {

      String url = DEPENDENCY_RELEASES.replaceFirst("%s", id[0]).replace("%s", id[1]);
      Request request = new Request.Builder().url(url).build();
      ResponseBody response = client.newCall(request).execute().body();
      DependencyRelease release =
          objectMapper.readValue(response.string(), DependencyRelease.class);
      System.out.println(
          release.getGroup() + ":" + release.getArtifact() + ":" + release.getVersion());

    } else {
      throw new IllegalArgumentException(
          dependency
              + "is not a valid dependency usage: gradle u "
              + "--dependency "
              + "dependency.group:artifact");
    }
  }

  private void updatePlugin(String lastRelease) throws IOException {

    if (lastRelease.equals(Utils.getVersionPlugin())) {
      logger.lifecycle("You are already using the latest version of the plugin");
      return;
    }
    logger.lifecycle("Updating Plugin ");

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
