package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class validateStructureTask extends DefaultTask {
    public String _package = "co.com.bancolombia";

    @TaskAction
    public void validateStructure() throws Exception {

        _package = Utils.readProperties("package");
        System.out.println("Clean Architecture plugin version: " + Utils.getVersionPlugin());
        System.out.println("Project Package: " + _package);
        _package = _package.replaceAll("\\.", "\\/");
        if (!validateModelLayer()) {
            throw new Exception("the model layer is invalid");
        }
        if (!validateUseCaseLayer()) {
            throw new Exception("the useCase layer is invalid");
        }
        System.out.println("The project is valid");

    }

    private boolean validateModelLayer() throws IOException {
        Stream<String> stream = Utils.readFile(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.buildGradle));

        long countImplementationproject = stream
                .map(line -> line.replaceAll(" ", ""))
                .filter(line -> !line.startsWith("//") && line.contains("implementationproject"))
                .count();

        return countImplementationproject == 0;
    }


    private boolean validateUseCaseLayer() {
        String modelDependency1 = "implementationproject(':domain-model')";
        String modelDependency2 = "compileproject(':domain-model')";

        Supplier<Stream<String>> stream = () -> {
            try {
                return Utils.readFile(getProject(), Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
        boolean isvalid = stream.get().filter(line -> !line.startsWith("//")).map(line -> line.replaceAll(" ", ""))
                .anyMatch(str -> str.equals(modelDependency1) || str.equals(modelDependency2));
        long countImplementationproject = stream.get().map(line -> line.replaceAll(" ", "")).filter(line -> !line.startsWith("//") && line.contains("implementationproject")).count();
        long countCompileproject = stream.get().map(line -> line.replaceAll(" ", "")).filter(line -> !line.startsWith("//") && line.contains("compileproject")).count();

        return isvalid && ((countImplementationproject == 1 && countCompileproject == 0) || (countImplementationproject == 0 && countCompileproject == 1));
    }

}

