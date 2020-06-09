package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class EntryPointRestWebflux implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.addParamPackage(Utils.readProperties("package"));
        builder.setupFromTemplate("entrypoint/rest-webflux");
        builder.appendSettings("reactive-web", "infrastructure/entry-points");
    }
}
