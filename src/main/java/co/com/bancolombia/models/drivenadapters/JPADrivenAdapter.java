package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.models.Module;
import co.com.bancolombia.templates.DrivenAdapterTemplate;
import co.com.bancolombia.templates.HelperTemplate;

public class JPADrivenAdapter extends Module {
    @Override
    public String getClassNameModule() {
        return DrivenAdapterTemplate.JPA_REPOSITORY_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate.getJPARepositoryClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getInterfaceNameModule() {
        return DrivenAdapterTemplate.JPA_REPOSITORY_INTERFACE;
    }

    @Override
    public String getModuleInterfaceContent() {
        return DrivenAdapterTemplate.getJPARepositoryInterfaceContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleModule() {
        return  HelperTemplate.getBuildGradleHelperJPARepository();
    }

    @Override
    public String getHelperModuleClassContent() {
        return  HelperTemplate.getHelperJPARepositoryClassContent(super.getPackageName().concat(".").concat(super.getHelperPackage()));
    }

    @Override
    public String getBuildGradleContentModule() {
        return DrivenAdapterTemplate.getBuildGradleJPARepository();
    }

    @Override
    public String getSettingsGradleModule() {
        return DrivenAdapterTemplate.getSettingsJPARepositoryContent().concat(HelperTemplate.getSettingsHelperJPAContent());
    }

    @Override
    public String getInterfaceModule() {
        return null;
    }
}
