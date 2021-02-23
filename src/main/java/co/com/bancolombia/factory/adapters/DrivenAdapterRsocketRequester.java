package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class DrivenAdapterRsocketRequester implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.loadPackage();
        builder.appendToSettings("rsocket-requester", "infrastructure/driven-adapters");
        builder.appendDependencyToModule("app-service",
                "implementation project(':rsocket-requester')");
        builder.setupFromTemplate("driven-adapter/rsocket-requester");
    }
}
