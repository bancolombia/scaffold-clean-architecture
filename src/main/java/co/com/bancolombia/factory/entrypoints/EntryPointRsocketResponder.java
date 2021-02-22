package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class EntryPointRsocketResponder implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.loadPackage();
        builder.appendToSettings("rsocket-responder", "infrastructure/entry-points");
        builder.appendToProperties("spring.rsocket.server")
                .put("port", 7000);
        builder.appendDependencyToModule("app-service",
                "implementation project(':rsocket-responder')");
        builder.setupFromTemplate("entry-point/rsocket-responder");
    }

}
