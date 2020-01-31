package co.com.bancolombia.factory;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.AbstractModule;

import java.io.IOException;

public interface ModuleFactory {
    AbstractModule makeDrivenAdapter(int codeDrivenAdapter) throws IOException, CleanException;
}
