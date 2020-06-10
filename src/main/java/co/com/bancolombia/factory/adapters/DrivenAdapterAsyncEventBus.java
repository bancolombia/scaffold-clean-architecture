package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class DrivenAdapterAsyncEventBus implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.addParamPackage(FileUtils.readProperties("package"));
        builder.setupFromTemplate("driven-adapter/async-event-bus");
        builder.appendToSettings("async-event-bus", "infrastructure/driven-adapter");
    }
}
