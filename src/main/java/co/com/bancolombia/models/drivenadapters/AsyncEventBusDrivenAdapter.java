package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.Module;

public class AsyncEventBusDrivenAdapter extends Module {
    @Override
    public String getClassNameModule() {
        return Constants.EVENT_BUS_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return Constants.getEventBusClassContent(super.getPackageName(), super.getModulePackage());
    }

    @Override
    public String getInterfaceNameModule() {
        return null;
    }

    @Override
    public String getModuleInterfaceContent() { return null; }

    @Override
    public String getBuildGradleModule() { return null; }

    @Override
    public String getHelperModuleClassContent() { return null; }

    @Override
    public String getBuildGradleContentModule() {
        return Constants.getBuildGradleEventBus();
    }

    @Override
    public String getSettingsGradleModule() {
        return Constants.getSettingsEventBusContent();
    }

    @Override
    public String getInterfaceModule() {
        return  Constants.getEventBusInterfaceContent(super.getPackageName().concat(".").concat(Constants.COMMON).concat(".").concat(Constants.GATEWAYS));
    }
}
