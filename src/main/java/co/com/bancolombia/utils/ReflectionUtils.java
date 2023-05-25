package co.com.bancolombia.utils;

import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.task.annotations.CATask;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.gradle.api.Task;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@UtilityClass
public class ReflectionUtils {

  public static Stream<Class<? extends ModuleFactory>> getModuleFactories(String packageName) {
    return new Reflections(packageName, Scanners.SubTypes)
        .getSubTypesOf(ModuleFactory.class).stream();
  }

  public static Stream<Class<? extends Task>> getTasks() {
    return new Reflections("co.com.bancolombia.task", Scanners.TypesAnnotated)
        .getTypesAnnotatedWith(CATask.class).stream()
            .filter(Task.class::isAssignableFrom)
            .map(c -> c.asSubclass(Task.class));
  }
}
