package co.com.bancolombia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.gradle.api.logging.Logger;

@AllArgsConstructor
public class CommandUtils {
  private static final String GIT_STATUS = "git status";
  private final Runtime rt;

  public static CommandUtils getDefault() {
    return new CommandUtils(Runtime.getRuntime());
  }

  public boolean hasGitPendingChanges(Logger logger) {
    try {
      Process process = rt.exec(GIT_STATUS);
      String output = processCommandOutput(process);
      return !output.contains("nothing to commit");
    } catch (IOException e) {
      logger.warn("Could not run git verification", e);
    }
    return false;
  }

  private String processCommandOutput(Process process) throws IOException {
    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return IOUtils.toString(input);
  }
}
