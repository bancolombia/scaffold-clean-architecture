package co.com.bancolombia.utils;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import lombok.Getter;

/**
 * Compiles generated OpenAPI domain model sources with Lombok (mirrors a real Gradle model module).
 */
public final class GeneratedDomainModelCompilation {
  private GeneratedDomainModelCompilation() {}

  public static void assertCompiles(Path javaSourceRoot) throws IOException {
    assumeTrue(
        ToolProvider.getSystemJavaCompiler() != null,
        "JDK with compiler (not JRE-only) is required for compilation check");
    List<File> sources = collectJavaSources(javaSourceRoot);
    if (sources.isEmpty()) {
      fail("No .java sources under " + javaSourceRoot);
    }
    String lombokJar = lombokJarPath();
    Path outputDir = Files.createTempDirectory("generated-domain-models-compile");
    try {
      compile(sources, outputDir, lombokJar);
    } finally {
      deleteRecursively(outputDir);
    }
  }

  private static void compile(List<File> sources, Path outputDir, String lombokJar)
      throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    List<String> options =
        List.of(
            "-d",
            outputDir.toString(),
            "--release",
            "21",
            "-classpath",
            lombokJar,
            "-processorpath",
            lombokJar,
            "-parameters");
    Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(sources);
    JavaCompiler.CompilationTask task =
        compiler.getTask(null, fileManager, diagnostics, options, null, units);
    boolean compiled = Boolean.TRUE.equals(task.call());
    fileManager.close();
    if (compiled) {
      return;
    }
    fail(formatDiagnostics(diagnostics, sources));
  }

  private static String formatDiagnostics(
      DiagnosticCollector<JavaFileObject> diagnostics, List<File> sources) {
    StringBuilder out = new StringBuilder("Generated domain models failed to compile.\n");
    boolean hasErrors = false;
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
      if (diagnostic.getKind() != Diagnostic.Kind.ERROR) {
        continue;
      }
      hasErrors = true;
      out.append(
          String.format(
              "%s:%d:%d: %s%n",
              diagnostic.getSource() == null ? "?" : diagnostic.getSource().getName(),
              diagnostic.getLineNumber(),
              diagnostic.getColumnNumber(),
              diagnostic.getMessage(null)));
    }
    if (!hasErrors) {
      out.append("Compiler returned failure without ERROR diagnostics.\n");
      out.append("Sources (").append(sources.size()).append("):\n");
      sources.forEach(file -> out.append("  ").append(file).append('\n'));
    }
    return out.toString();
  }

  private static List<File> collectJavaSources(Path javaSourceRoot) throws IOException {
    if (!Files.isDirectory(javaSourceRoot)) {
      return List.of();
    }
    List<File> files = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(javaSourceRoot)) {
      paths.filter(path -> path.toString().endsWith(".java")).map(Path::toFile).forEach(files::add);
    }
    return files;
  }

  private static String lombokJarPath() {
    try {
      return Path.of(Getter.class.getProtectionDomain().getCodeSource().getLocation().toURI())
          .toString();
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Unable to resolve Lombok jar on the test classpath", e);
    }
  }

  private static void deleteRecursively(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }
    try (Stream<Path> paths = Files.walk(dir)) {
      paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
    }
  }
}
