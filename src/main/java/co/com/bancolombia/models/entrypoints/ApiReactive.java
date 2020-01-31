package co.com.bancolombia.models.entrypoints;

import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.templates.EntryPointTemplate;

import java.io.IOException;

public class ApiReactive extends AbstractModule {
    public ApiReactive() throws IOException {
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
    public String getInterfaceNameModule() {
        return null;
    }

    @Override
    public String getModuleInterfaceContent() {
        return null;
    }

    @Override
    public String getBuildGradleModule() {
        return null;
    }

    @Override
    public String getHelperModuleClassContent() {
        return null;
    }

    @Override
    public String getBuildGradleContentModule() {
        return EntryPointTemplate.getBuildGradleReactiveWeb();
    }

    @Override
    public String getSettingsGradleModule() {
        return EntryPointTemplate.getSettingsReactiveWebContent();
    }

    @Override
    public String getInterfaceModule() {
        return null;
    }
}
