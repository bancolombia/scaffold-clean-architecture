package co.com.bancolombia.factory;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.Module;
import co.com.bancolombia.models.entrypoints.ApiReactive;
import co.com.bancolombia.models.entrypoints.ApiRest;
import co.com.bancolombia.templates.EntryPointTemplate;

import java.io.IOException;

public class EntryPointFactoryImpl implements ModuleFactory {

    @Override
    public Module makeDrivenAdapter(int codeEntryPoint) throws IOException, CleanException {
        Module entryPoint = null;
        if (EntryPointTemplate.getNameEntryPoint(codeEntryPoint) == null) {
            throw new IllegalArgumentException("Entry Point not is available (1 -> API REST, 2 -> API REACTIVE)");
        }

        switch (codeEntryPoint) {
            case 1:
                entryPoint = new ApiRest();
                entryPoint.setName("api-rest");
                entryPoint.setModulePackage("api");
                break;
            case 2:
                entryPoint = new ApiReactive();
                entryPoint.setName("reactive-web");
                entryPoint.setModulePackage("api");
                break;
            default:
                throw new CleanException("Entry Point invalid");

        }

        entryPoint.setPackageName(Utils.readProperties("package"));
        entryPoint.setPackageName(entryPoint.getPackageName().replaceAll("\\.", "\\/"));

        entryPoint.setModuleDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.ENTRY_POINTS).concat("/").concat(entryPoint.getName()));

        return entryPoint;
    }
}
