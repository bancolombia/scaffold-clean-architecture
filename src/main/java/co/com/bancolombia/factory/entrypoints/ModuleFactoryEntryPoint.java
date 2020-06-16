package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;

public class ModuleFactoryEntryPoint {

    public static ModuleFactory getEntryPointFactory(EntryPointType type) throws InvalidTaskOptionException {
        switch (type) {
            case RESTMVC:
                return new EntryPointRestMvc();
            case WEBFLUX:
                return new EntryPointRestWebflux();
            case GENERIC:
                return new EntryPointGeneric();
            default:
                throw new InvalidTaskOptionException("Entry Point type invalid");
        }
    }

    public enum EntryPointType {
        RESTMVC, WEBFLUX, GENERIC
    }
}
