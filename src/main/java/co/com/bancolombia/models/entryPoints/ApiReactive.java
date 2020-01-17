package co.com.bancolombia.models.entryPoints;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.Module;

public class ApiReactive extends Module {
    @Override
    public String getClassNameModule() {
        return Constants.API_REST_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return Constants.getReactiveWebClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
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
        return Constants.getBuildGradleReactiveWeb();
    }

    @Override
    public String getSettingsGradleModule() {
        return Constants.getSettingsReactiveWebContent();
    }
}
