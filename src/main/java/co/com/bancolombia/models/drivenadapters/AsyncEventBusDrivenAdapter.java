package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.templates.DrivenAdapterTemplate;

import java.io.IOException;

public class AsyncEventBusDrivenAdapter extends AbstractModule {
    public AsyncEventBusDrivenAdapter() throws IOException {
        super();
        super.setName("async-event-bus");
        super.setModulePackage("events");
        super.setModelName(DrivenAdapterTemplate.EVENT_BUS_GATEWAY_CLASS);
        super.setGatewayName("Event");
        super.setGatewayDir(Constants.DOMAIN.concat("/")
                .concat(Constants.MODEL).concat("/")
                .concat(Constants.MAIN_JAVA).concat("/")
                .concat(super.getPackageName()).concat("/")
                .concat(Constants.MODEL).concat("/")
                .concat(DrivenAdapterTemplate.EVENT_BUS_MODEL_NAME).concat("/")
                .concat(Constants.GATEWAYS));
        super.setModuleDir(Constants.INFRASTRUCTURE
                .concat("/").concat(Constants.DRIVEN_ADAPTERS)
                .concat("/").concat(super.getName()));
        if (super.hasHelperModule()) {
            super.setHelperDir(Constants.INFRASTRUCTURE
                    .concat("/").concat(Constants.HELPERS)
                    .concat("/").concat(super.getNameHelper()));
        }
    }

    @Override
    public String getClassNameModule() {
        return DrivenAdapterTemplate.EVENT_BUS_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate
                .getEventBusClassContent(super.getPackageName(), super.getModulePackage());
    }

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
        return DrivenAdapterTemplate
                .getEventBusInterfaceContent(super.getPackageName()
                        .concat(".").concat(DrivenAdapterTemplate.COMMON)
                        .concat(".").concat(Constants.GATEWAYS));
    }

    @Override
    public String getAppServiceImports() {
        return "";
    }

    @Override
    public String getPropertiesFileContent() {
        return null;
    }
}
