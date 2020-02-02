package co.com.bancolombia.factory;

import co.com.bancolombia.Utils;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.models.entrypoints.ApiReactive;
import co.com.bancolombia.models.entrypoints.ApiRest;
import co.com.bancolombia.templates.EntryPointTemplate;

import java.io.IOException;

public class EntryPointFactory implements ModuleFactory {

    @Override
    public AbstractModule makeDrivenAdapter(int codeEntryPoint) throws IOException, CleanException {
        throwFactory(codeEntryPoint);

        AbstractModule entryPoint = null;
        switch (codeEntryPoint) {
            case 1:
                entryPoint = new ApiRest();
                break;
            case 2:
                entryPoint = new ApiReactive();
                break;
            default:
                throw new CleanException("Entry Point invalid");

        }
        entryPoint.setCode(codeEntryPoint);

        return entryPoint;
    }

    private void throwFactory(int codeEntryPoint) {
        if (EntryPointTemplate.getNameEntryPoint(codeEntryPoint) == null) {
            String entryPointsAvailables = "Entry Point not is available " +
                    "(1 -> API REST, 2 -> API REACTIVE)";
            throw new IllegalArgumentException(entryPointsAvailables);
        }
    }
}
