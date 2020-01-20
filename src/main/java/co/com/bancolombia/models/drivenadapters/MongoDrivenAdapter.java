package co.com.bancolombia.models.drivenadapters;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.Module;

public class MongoDrivenAdapter extends Module {
    @Override
    public String getClassNameModule() {
        return Constants.MONGO_REPOSITORY_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return Constants.getMongoRepositoryClassContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getInterfaceNameModule() {
        return Constants.MONGO_REPOSITORY_INTERFACE;
    }

    @Override
    public String getModuleInterfaceContent() {
        return Constants.getMongoRepositoryInterfaceContent(super.getPackageName().concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleModule() {
        return  Constants.getBuildGradleHelperMongoRepository();
    }

    @Override
    public String getHelperModuleClassContent() {
        return  Constants.getHelperMongoRepositoryClassContent(super.getPackageName().concat(".").concat(super.getHelperPackage()));
    }

    @Override
    public String getBuildGradleContentModule() {
        return Constants.getBuildGradleMongoRepository();
    }

    @Override
    public String getSettingsGradleModule() {
        return Constants.getSettingsMongoRepositoryContent().concat(Constants.getSettingsHelperMongoContent());
    }

    @Override
    public String getInterfaceModule() {
        return null;
    }
}
