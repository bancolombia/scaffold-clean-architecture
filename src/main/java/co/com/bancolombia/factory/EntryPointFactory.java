package co.com.bancolombia.factory;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.models.entrypoints.ApiReactive;
import co.com.bancolombia.models.entrypoints.ApiRest;
import co.com.bancolombia.templates.EntryPointTemplate;

import java.io.IOException;

import static co.com.bancolombia.templates.EntryPointTemplate.EntryPoints.*;

public class EntryPointFactory implements ModuleFactory {

    @Override
    public AbstractModule makeDrivenAdapter(int codeEntryPoint) throws IOException, CleanException {
        throwFactory(codeEntryPoint);

        AbstractModule entryPoint = null;
        switch (throwFactory(codeEntryPoint)) {
            case API_REST_IMPERATIVE:
                entryPoint = new ApiRest();
                break;
            case API_REST_REACTIVE:
                entryPoint = new ApiReactive();
                break;
            default:
                throw new CleanException("Entry Point invalid");

        }
        entryPoint.setCode(codeEntryPoint);

        return entryPoint;
    }

    private EntryPointTemplate.EntryPoints throwFactory(int codeEntryPoint) {

        return EntryPointTemplate.EntryPoints.valueOf(codeEntryPoint, () -> NO_AVAILABLE);

    }

}
