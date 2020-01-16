package co.com.bancolombia.factory;

import co.com.bancolombia.models.DrivenAdapter;

import java.io.IOException;

public interface DrivenAdapterFactory {
    DrivenAdapter makeDrivenAdapter(int codeDrivenAdapter) throws IOException;
}
