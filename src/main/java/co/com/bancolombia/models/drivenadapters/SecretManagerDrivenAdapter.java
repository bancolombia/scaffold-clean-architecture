package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.templates.DrivenAdapterTemplate;

import java.io.IOException;

public class SecretManagerDrivenAdapter extends AbstractModule {

    public SecretManagerDrivenAdapter() throws IOException {
        super();
        super.setName("secrets-manager-consumer");
        super.setModulePackage("secrets");
        super.setModelName(DrivenAdapterTemplate.SECRET_MANAGER_CONSUMER_CLASS);
        super.setModelDir(Constants.DOMAIN
                .concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA)
                .concat("/").concat(super.getPackageName()));
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
        return DrivenAdapterTemplate.SECRET_MANAGER_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate
                .getSecretsManagerClassContent(super.getPackageName(), super.getModulePackage());
    }

    @Override
    public String getBuildGradleContentModule() {
        return DrivenAdapterTemplate.getBuildGradleSecretsManager();
    }

    @Override
    public String getSettingsGradleModule() {
        return DrivenAdapterTemplate.getSettingsSecretsManagerContent();
    }

    @Override
    public String getInterfaceModule() {
        return DrivenAdapterTemplate
                .getSecretsManagerInterfaceContent(super.getPackageName()
                        .concat(".").concat(DrivenAdapterTemplate.COMMON).concat(".")
                        .concat(Constants.GATEWAYS));
    }
}
