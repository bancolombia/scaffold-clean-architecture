package co.com.bancolombia.models;

import co.com.bancolombia.Constants;

public class MongoDrivenAdapter extends DrivenAdapter {
    @Override
    public String getClassNameEntryPoint() {
        return Constants.MONGO_REPOSITORY_CLASS;
    }

    @Override
    public String getDrivenAdapterClassContent() {
        return Constants.getMongoRepositoryClassContent(super.getPackageName().concat(".").concat(super.getDrivenAdapterPackage()));
    }

    @Override
    public String getInterfaceNameEntryPoint() {
        return Constants.MONGO_REPOSITORY_INTERFACE;
    }

    @Override
    public String getDrivenAdapterInterfaceContent() {
        return Constants.getMongoRepositoryInterfaceContent(super.getPackageName().concat(".").concat(super.getDrivenAdapterPackage()));
    }

    @Override
    public String getBuildGradleDrivenAdapter() {
        return  Constants.getBuildGradleHelperMongoRepository();
    }

    @Override
    public String getHelperDrivenAdapterClassContent() {
        return  Constants.getHelperMongoRepositoryClassContent(super.getPackageName().concat(".").concat(super.getHelperDrivenAdapterPackage()));
    }

    @Override
    public String getBuildGradleContentDrivenAdapter() {
        return Constants.getBuildGradleMongoRepository();
    }

    @Override
    public String getSettingsGradleDrivenAdapter() {
        return Constants.getSettingsMongoRepositoryContent().concat(Constants.getSettingsHelperMongoContent());
    }
}
