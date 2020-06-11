package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;

public class ModuleFactoryDrivenAdapter {

    public static ModuleFactory getDrivenAdapterFactory(DrivenAdapterType type) throws InvalidTaskOptionException {
        switch (type) {
            case JPA: {
                return new DrivenAdapterJPA();
            }
            case MONGODB: {
                return new DrivenAdapterMongoDB();
            }
            case ASYNCEVENTBUS: {
                return new DrivenAdapterAsyncEventBus();
            }
            case GENERIC: {
                return new DrivenAdapterGeneric();
            }
            default: {
                throw new InvalidTaskOptionException("Driven Adapter type invalid");
            }
        }
    }

    public enum DrivenAdapterType {
        JPA, MONGODB, ASYNCEVENTBUS, GENERIC
    }
}
