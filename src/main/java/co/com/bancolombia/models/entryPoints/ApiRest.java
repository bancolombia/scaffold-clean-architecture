package co.com.bancolombia.models.entryPoints;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.Module;

public class ApiRest extends Module {
    @Override
    public String getClassNameModule() {
        return Constants.API_REST_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return Constants.getApiRestClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
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
        return Constants.getBuildGradleApiRest();
    }

    @Override
    public String getSettingsGradleModule() {
        return Constants.getSettingsApiRestContent();
    }
}
