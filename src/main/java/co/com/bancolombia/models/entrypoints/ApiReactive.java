package co.com.bancolombia.models.entrypoints;

import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.EntryPointTemplate;

import java.io.IOException;

public class ApiReactive extends AbstractModule {

    public ApiReactive() throws IOException {
        super();
        super.setName("reactive-web");
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
                .getReactiveWebClassContent(super.getPackageName()
                        .concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleContentModule() {
        return EntryPointTemplate.getBuildGradleReactiveWeb();
    }

    @Override
    public String getSettingsGradleModule() {
        return EntryPointTemplate.getSettingsReactiveWebContent();
    }

}
