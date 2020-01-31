package co.com.bancolombia.factory;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.models.drivenadapters.AsyncEventBusDrivenAdapter;
import co.com.bancolombia.models.drivenadapters.JPADrivenAdapter;
import co.com.bancolombia.models.drivenadapters.MongoDrivenAdapter;
import co.com.bancolombia.models.drivenadapters.SecretManagerDrivenAdapter;
import co.com.bancolombia.templates.DrivenAdapterTemplate;

import java.io.IOException;

public class DrivenAdapterFactoryImpl implements ModuleFactory {

    @Override
    public AbstractModule makeDrivenAdapter(int codeDrivenAdapter) throws IOException, CleanException {
        if (DrivenAdapterTemplate.getNameDrivenAdapter(codeDrivenAdapter) == null) {
            throw new IllegalArgumentException("Entry Point not is available (1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer, 4 -> Async Event Bus )");
        }
        AbstractModule drivenAdapter = null;
        switch (codeDrivenAdapter) {
            case 1:
                drivenAdapter = new JPADrivenAdapter();
                break;
            case 2:
                drivenAdapter = new MongoDrivenAdapter();
                break;
            case 3:
                drivenAdapter = new SecretManagerDrivenAdapter();
                break;
            case 4:
                drivenAdapter = new AsyncEventBusDrivenAdapter();
                break;
            default:
                throw new CleanException("Driven Adapter invalid");
        }
        drivenAdapter.setCode(codeDrivenAdapter);

        return drivenAdapter;
    }
}
