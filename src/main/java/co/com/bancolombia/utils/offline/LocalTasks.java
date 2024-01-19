package co.com.bancolombia.utils.offline;

import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalTasks {

  public static void main(String[] args) throws IOException {
    // When needed you can use args[0] to determine task to run
    UpdateDependencies.ofDefaults().run();
  }
}
