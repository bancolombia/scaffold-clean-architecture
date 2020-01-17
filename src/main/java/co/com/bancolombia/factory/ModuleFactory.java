package co.com.bancolombia.factory;

import co.com.bancolombia.models.Module;

import java.io.IOException;

public interface ModuleFactory {
    Module makeDrivenAdapter(int codeDrivenAdapter) throws IOException;
}
