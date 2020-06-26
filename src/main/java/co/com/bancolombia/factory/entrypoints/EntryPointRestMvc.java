package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class EntryPointRestMvc implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.loadPackage();
        builder.setupFromTemplate("entry-point/rest-mvc");
        builder.appendToSettings("api-rest", "infrastructure/entry-points");
        builder.appendDependencyToModule("app-service", "implementation project(':api-rest')");
    }
}
