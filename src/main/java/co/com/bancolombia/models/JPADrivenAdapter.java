package co.com.bancolombia.models;

import co.com.bancolombia.Constants;

public class JPADrivenAdapter extends DrivenAdapter {
    @Override
    public String getClassNameEntryPoint() {
        return Constants.JPA_REPOSITORY_CLASS;
    }

    @Override
    public String getDrivenAdapterClassContent() {
        return Constants.getJPARepositoryClassContent(super.getPackageName().concat(".").concat(super.getDrivenAdapterPackage()));
    }

    @Override
    public String getInterfaceNameEntryPoint() {
        return Constants.JPA_REPOSITORY_INTERFACE;
    }

    @Override
    public String getDrivenAdapterInterfaceContent() {
        return Constants.getJPARepositoryInterfaceContent(super.getPackageName().concat(".").concat(super.getDrivenAdapterPackage()));
    }

    @Override
    public String getBuildGradleDrivenAdapter() {
        return  Constants.getBuildGradleHelperJPARepository();
    }

    @Override
    public String getHelperDrivenAdapterClassContent() {
        return  Constants.getHelperJPARepositoryClassContent(super.getPackageName().concat(".").concat(super.getHelperDrivenAdapterPackage()));
    }

    @Override
    public String getBuildGradleContentDrivenAdapter() {
        return Constants.getBuildGradleJPARepository();
    }

    @Override
    public String getSettingsGradleDrivenAdapter() {
        return Constants.getSettingsJPARepositoryContent().concat(Constants.getSettingsHelperJPAContent());
    }


}
