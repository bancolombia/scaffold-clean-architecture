package co.com.bancolombia.models.entrypoints;

import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.EntryPointTemplate;

import java.io.IOException;

public class ApiRest extends AbstractModule {

    public ApiRest() throws IOException {
        super();
        super.setName("api-rest");
        super.setModulePackage("api");
        super.setModuleDir(Constants.INFRASTRUCTURE
                .concat("/").concat(Constants.ENTRY_POINTS)
                .concat("/").concat(getName()));
    }

    @Override
    public String getClassNameModule() {
        return EntryPointTemplate.API_REST_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return EntryPointTemplate
                .getApiRestClassContent(super.getPackageName()
                        .concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleContentModule() {
        return EntryPointTemplate.getBuildGradleApiRest();
    }

    @Override
    public String getSettingsGradleModule() {
        return EntryPointTemplate.getSettingsApiRestContent();
    }

}
