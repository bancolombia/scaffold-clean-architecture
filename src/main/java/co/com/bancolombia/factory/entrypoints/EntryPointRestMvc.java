package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class EntryPointRestMvc implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.addParamPackage(Utils.readProperties("package"));
        builder.setupFromTemplate("entrypoint/rest-mvc");
        builder.appendSettings("api-rest", "infrastructure/entry-points");
    }
}
