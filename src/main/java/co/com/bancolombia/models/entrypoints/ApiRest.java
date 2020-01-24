package co.com.bancolombia.models.entrypoints;

import co.com.bancolombia.models.Module;
import co.com.bancolombia.templates.EntryPointTemplate;

public class ApiRest extends Module {
    @Override
    public String getClassNameModule() {
        return EntryPointTemplate.API_REST_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return EntryPointTemplate.getApiRestClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
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
        return EntryPointTemplate.getBuildGradleApiRest();
    }

    @Override
    public String getSettingsGradleModule() {
        return EntryPointTemplate.getSettingsApiRestContent();
    }

    @Override
    public String getInterfaceModule() {
        return null;
    }
}
