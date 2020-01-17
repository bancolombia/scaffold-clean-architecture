package co.com.bancolombia.models;

import co.com.bancolombia.Constants;

public class SecretManagerDrivenAdapter extends DrivenAdapter {
    @Override
    public String getClassNameEntryPoint() {
        return Constants.SECRET_MANAGER_CLASS;
    }

    @Override
    public String getDrivenAdapterClassContent() {
        return Constants.getSecretsManagerClassContent(super.getPackageName(), super.getDrivenAdapterPackage());
    }
    @Override
    public String getInterfaceNameEntryPoint() {
        return null;
    }

    @Override
    public String getDrivenAdapterInterfaceContent() {
        return null;
    }
    @Override
    public String getBuildGradleDrivenAdapter() {
        return  null;
    }

    @Override
    public String getHelperDrivenAdapterClassContent() {
        return null;
    }

    @Override
    public String getBuildGradleContentDrivenAdapter() {
        return Constants.getBuildGradleSecretsManager();
    }

    @Override
    public String getSettingsGradleDrivenAdapter() {
        return Constants.getSettingsSecretsManagerContent();
    }
}
