package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateDrivenAdapterTask extends DefaultTask {
    private int numberDrivenAdapter = -1;
    Logger logger = LoggerFactory.getLogger(GenerateDrivenAdapterTask.class);

    @Option(option = "value", description = "Set the number of the entry point")
    public void setDrivenAdapter(String number) {
        if (!number.isEmpty()) {
            this.numberDrivenAdapter = Integer.parseInt(number);
        }
    }

    @TaskAction
    public void generateDrivenAdapter() throws Exception {
        String packageName;
        String nameDrivenAdapter;
        if (numberDrivenAdapter < 0) {
            throw new IllegalArgumentException("No Entry Point is set, usege: gradle generateEntryPoint --value numberEntryPoint");
        }

        nameDrivenAdapter = Constants.getNameDrivenAdapter(numberDrivenAdapter);
        if (nameDrivenAdapter.isEmpty()) {
            throw new IllegalArgumentException("Entry Point not is available");
        }
        packageName = Utils.readProperties("package");

        logger.info("Clean Architecture plugin version: {0}", Utils.getVersionPlugin());
        logger.info("Project  Package: {0}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Driven Adapter: {0} - {1}", numberDrivenAdapter, nameDrivenAdapter);


        switch (numberDrivenAdapter){
            case 1:
                generateJPARepository(packageName);
                break;
            case 2:
                generateMongoRepository(packageName);
                break;
            case 3:
                generateSecretsManager(packageName);
                break;
            default:
                break;
        }
    }

    private void generateSecretsManager(String packageName) {
    }

    private void generateMongoRepository(String packageName) {
    }

    private void generateJPARepository(String packageName) {
    }
}
