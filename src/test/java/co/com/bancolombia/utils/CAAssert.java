package co.com.bancolombia.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Assert;

public class CAAssert extends Assert {
  private CAAssert() {}

  public static void assertFilesExists(String... files) {
    for (String file : files) {
      assertTrue("File: " + file + " not exists", Files.exists(Path.of(file)));
    }
  }

  public static void assertFilesExistsInDir(String dir, String... files) {
    assertFilesExists(
        Arrays.stream(files)
            .map(file -> dir + file)
            .collect(Collectors.toList())
            .toArray(new String[files.length]));
  }
}
