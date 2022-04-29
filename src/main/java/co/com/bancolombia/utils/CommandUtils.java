package co.com.bancolombia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;

@AllArgsConstructor
public class CommandUtils {
  private final Runtime rt;

  public static CommandUtils getDefault() {
    return new CommandUtils(Runtime.getRuntime());
  }

  public boolean hasGitPendingChanges() {
    try {
      Process process = rt.exec("git status");
      String output = processCommandOutput(process);
      return !output.contains("nothing to commit");
    } catch (IOException ignored) {
    }
    return false;
  }

  private String processCommandOutput(Process process) throws IOException {
    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return IOUtils.toString(input);
  }
}
