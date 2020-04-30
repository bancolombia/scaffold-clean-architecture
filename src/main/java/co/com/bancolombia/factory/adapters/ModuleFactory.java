package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.adapters.AbstractModule;

import java.io.IOException;

public interface ModuleFactory {
    AbstractModule makeDrivenAdapter(int codeDrivenAdapter) throws IOException, CleanException;
}
