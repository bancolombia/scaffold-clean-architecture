package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.models.Module;
import co.com.bancolombia.templates.DrivenAdapterTemplate;

public class AsyncEventBusDrivenAdapter extends Module {
    @Override
    public String getClassNameModule() {
        return DrivenAdapterTemplate.EVENT_BUS_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate.getEventBusClassContent(super.getPackageName(), super.getModulePackage());
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
        return DrivenAdapterTemplate.getBuildGradleEventBus();
    }

    @Override
    public String getSettingsGradleModule() {
        return DrivenAdapterTemplate.getSettingsEventBusContent();
    }

    @Override
    public String getInterfaceModule() {
        return  DrivenAdapterTemplate.getEventBusInterfaceContent(super.getPackageName().concat(".").concat(DrivenAdapterTemplate.COMMON).concat(".").concat(Constants.GATEWAYS));
    }
}
