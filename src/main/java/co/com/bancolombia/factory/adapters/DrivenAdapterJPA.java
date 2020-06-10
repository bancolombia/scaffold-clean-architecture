package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.FileUtils;

import java.io.IOException;

public class DrivenAdapterJPA implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.addParamPackage(FileUtils.readProperties("package"));
        builder.setupFromTemplate("helper/jpa-repository-commons");
        builder.setupFromTemplate("driven-adapter/jpa-repository");
        builder.appendToSettings("jpa-repository", "infrastructure/driven-adapters");
        builder.appendToSettings("jpa-repository-commons", "infrastructure/helpers");
        builder.appendToProperties("spring.datasource").put("url", "jdbc:h2:mem:testdb");
        builder.appendToProperties("spring.datasource").put("driver-class-name", "org.h2.Driver");
        builder.appendDependencyToModule("app-service", "implementation project(':jpa-repository')");
        // TODO: Generate JPA Config validate secrets manager usage
    }
}
