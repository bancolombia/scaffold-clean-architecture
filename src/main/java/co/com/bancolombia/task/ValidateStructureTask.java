package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class ValidateStructureTask extends DefaultTask {
    private Logger logger = getProject().getLogger();

    @TaskAction
    public void validateStructure() throws IOException, CleanException {

        String packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project Package: {}", packageName);
        if (!validateModelLayer()) {
            throw new CleanException("the model layer is invalid");
        }
        if (!validateUseCaseLayer()) {
            throw new CleanException("the use case layer is invalid");
        }
        if (!validateEntryPointLayer()) {
            throw new CleanException("the entry point layer is invalid");
        }
        if (!validateDrivenAdapterLayer()) {
            throw new CleanException("the entry point layer is invalid");
        }
        logger.lifecycle("The project is valid");

    }
    //TODO: Complete
    private boolean validateEntryPointLayer() throws IOException {
        List<File> files = Utils.finderSubProjects(getProject().getProjectDir().getAbsolutePath().concat("/infrastructure/entry-points"));
        for (File file : files) {
            logger.lifecycle(file.getCanonicalPath());
        }
        return true;
    }

    //TODO: Complete
    private boolean validateDrivenAdapterLayer() throws IOException {
        List<File> files = Utils.finderSubProjects(getProject().getProjectDir().getAbsolutePath().concat("/infrastructure/driven-adapters"));
        for (File file : files) {
            logger.lifecycle(file.getCanonicalPath());
        }
        return true;
    }

    private boolean validateModelLayer() throws IOException {
        Stream<String> stream = Utils.readFile(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.BUILD_GRADLE));

        long countImplementationproject = stream
                .map(line -> line.replaceAll(" ", ""))
                .filter(line -> !line.startsWith("//") && line.contains("implementationproject"))
                .count();
        return countImplementationproject == 0;
    }


    private boolean validateUseCaseLayer() {
        String modelDependency1 = "implementationproject(':model')";
        String modelDependency2 = "compileproject(':model')";

        Supplier<Stream<String>> stream = () -> {
            try {
                return Utils.readFile(getProject(), Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.BUILD_GRADLE));
            } catch (IOException e) {
                logger.error(e.getMessage());
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

