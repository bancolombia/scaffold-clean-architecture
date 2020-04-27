package co.com.bancolombia.factory;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.models.drivenadapters.AsyncEventBusDrivenAdapter;
import co.com.bancolombia.models.drivenadapters.JPADrivenAdapter;
import co.com.bancolombia.models.drivenadapters.MongoDrivenAdapter;
import co.com.bancolombia.templates.DrivenAdapterTemplate;

import java.io.IOException;

import static co.com.bancolombia.templates.DrivenAdapterTemplate.DrivenAdapters.*;

public class DrivenAdapterFactory implements ModuleFactory {

    @Override
    public AbstractModule makeDrivenAdapter(int codeDrivenAdapter) throws IOException, CleanException {

        AbstractModule drivenAdapter = null;
        switch (throwFactory(codeDrivenAdapter)) {
            case JPA_REPOSITORY:
                drivenAdapter = new JPADrivenAdapter();
                break;
            case MONGO_REPOSITORY:
                drivenAdapter = new MongoDrivenAdapter();
                break;
            case ASYNC_EVENT_BUS:
                drivenAdapter = new AsyncEventBusDrivenAdapter();
                break;
            default:
                throw new CleanException("Driven Adapter invalid");
        }
        drivenAdapter.setCode(codeDrivenAdapter);

        return drivenAdapter;
    }

    private DrivenAdapterTemplate.DrivenAdapters throwFactory(int codeDrivenAdapter) {

        return DrivenAdapterTemplate.DrivenAdapters.valueOf(codeDrivenAdapter, () -> NO_AVAILABLE);

    }
}
