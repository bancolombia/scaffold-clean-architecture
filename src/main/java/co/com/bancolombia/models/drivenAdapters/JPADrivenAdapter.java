package co.com.bancolombia.models.drivenAdapters;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.Module;

public class JPADrivenAdapter extends Module {
    @Override
    public String getClassNameModule() {
        return Constants.JPA_REPOSITORY_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return Constants.getJPARepositoryClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getInterfaceNameModule() {
        return Constants.JPA_REPOSITORY_INTERFACE;
    }

    @Override
    public String getModuleInterfaceContent() {
        return Constants.getJPARepositoryInterfaceContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleModule() {
        return  Constants.getBuildGradleHelperJPARepository();
    }

    @Override
    public String getHelperModuleClassContent() {
        return  Constants.getHelperJPARepositoryClassContent(super.getPackageName().concat(".").concat(super.getHelperPackage()));
    }

    @Override
    public String getBuildGradleContentModule() {
        return Constants.getBuildGradleJPARepository();
    }

    @Override
    public String getSettingsGradleModule() {
        return Constants.getSettingsJPARepositoryContent().concat(Constants.getSettingsHelperJPAContent());
    }


}
