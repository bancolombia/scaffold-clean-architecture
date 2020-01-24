package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.models.Module;
import co.com.bancolombia.templates.DrivenAdapterTemplate;
import co.com.bancolombia.templates.HelperTemplate;

public class MongoDrivenAdapter extends Module {
    @Override
    public String getClassNameModule() {
        return DrivenAdapterTemplate.MONGO_REPOSITORY_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate.getMongoRepositoryClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getInterfaceNameModule() {
        return DrivenAdapterTemplate.MONGO_REPOSITORY_INTERFACE;
    }

    @Override
    public String getModuleInterfaceContent() {
        return DrivenAdapterTemplate.getMongoRepositoryInterfaceContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleModule() {
        return  HelperTemplate.getBuildGradleHelperMongoRepository();
    }

    @Override
    public String getHelperModuleClassContent() {
        return  HelperTemplate.getHelperMongoRepositoryClassContent(super.getPackageName().concat(".").concat(super.getHelperPackage()));
    }

    @Override
    public String getBuildGradleContentModule() {
        return DrivenAdapterTemplate.getBuildGradleMongoRepository();
    }

    @Override
    public String getSettingsGradleModule() {
        return DrivenAdapterTemplate.getSettingsMongoRepositoryContent().concat(HelperTemplate.getSettingsHelperMongoContent());
    }

    @Override
    public String getInterfaceModule() {
        return null;
    }
}
