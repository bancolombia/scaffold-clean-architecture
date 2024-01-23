package co.com.bancolombia;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.SimplePathVisitor;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;

public class TestUtils extends Assertions {

  private TestUtils() {}

  // Assertions

  public static void assertFilesExists(String... files) {
    for (String file : files) {
      assertTrue(Files.exists(Path.of(file)), () -> "File: " + file + " not exists");
    }
  }

  public static void assertFileContains(String file, String... toIncludes) {
    String content = "";
    try {
      content = FileUtils.readFileToString(new File(file), StandardCharsets.UTF_8);
    } catch (Exception e) {
      assertionFailure().message(e.getMessage()).expected(true).actual(false).buildAndThrow();
    }
    for (String toInclude : toIncludes) {
      assertTrue(content.contains(toInclude));
    }
  }

  public static void assertFilesExistsInDir(String dir, String... files) {
    final String realDir = dir.endsWith("/") ? dir : dir + "/";
    //noinspection SimplifyStreamApiCallChains
    assertFilesExists(
        Arrays.stream(files)
            .map(file -> realDir + file)
            .collect(Collectors.toList())
            .toArray(new String[files.length]));
  }

  // Setup
  @SafeVarargs
  public static Project setupProject(Class<?> testClassName, Class<? extends Task>... tasks) {
    final String dir = getTestDir(testClassName);
    final File dirFile = new File(dir);
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withGradleUserHomeDir(dirFile)
            .withProjectDir(dirFile)
            .build();
    for (Class<? extends Task> task : tasks) {
      project.getTasks().create(task.getSimpleName(), task);
    }
    return project;
  }

  public static String getTestDir(Class<?> clazz) {
    return "build/unitTestCa/" + clazz.getSimpleName();
  }

  public static <T extends Task> T getTask(Project project, Class<T> taskClass) {
    //noinspection unchecked
    return (T) project.getTasks().getByName(taskClass.getSimpleName());
  }

  public static <T extends Task> T createTask(Project project, Class<T> taskClass) {
    project.getTasks().create(taskClass.getSimpleName(), taskClass);
    return getTask(project, taskClass);
  }

  public static void runCleanTask(Project project) {
    Task task2 = project.getTasks().getByName("clean");
    task2.getActions().get(0).execute(task2);
  }

  public static void deleteStructure(Path sourcePath) {
    try {
      if (!Files.exists(sourcePath)) {
        return;
      }
      Files.walkFileTree(
          sourcePath,
          new SimplePathVisitor() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }
          });
      Files.delete(sourcePath);
    } catch (IOException e) {
      System.out.println("error delete Structure " + e.getMessage());
    }
  }
}
