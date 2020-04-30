package co.com.bancolombia.models.adapters.drivenadapters;

import co.com.bancolombia.models.adapters.AbstractModule;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.DrivenAdapterTemplate;
import co.com.bancolombia.templates.HelperTemplate;

import java.io.IOException;

public class MongoDrivenAdapter extends AbstractModule {

    public MongoDrivenAdapter() throws IOException {
        super();
        super.setName("mongo-repository");
        super.setNameHelper("mongo-repository-commons");
        super.setModulePackage("mongo");
        super.setHelperPackage("mongo");
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
        return DrivenAdapterTemplate.MONGO_REPOSITORY_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate
                .getMongoRepositoryClassContent(super.getPackageName()
                        .concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getInterfaceNameModule() {
        return DrivenAdapterTemplate.MONGO_REPOSITORY_INTERFACE;
    }

    @Override
    public String getModuleInterfaceContent() {
        return DrivenAdapterTemplate
                .getMongoRepositoryInterfaceContent(super.getPackageName()
                        .concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleModule() {
        return HelperTemplate.getBuildGradleHelperMongoRepository();
    }

    @Override
    public String getHelperModuleClassContent() {
        return HelperTemplate
                .getHelperMongoRepositoryClassContent(super.getPackageName()
                        .concat(".").concat(super.getHelperPackage()));
    }

    @Override
    public String getAppServiceImports() {
        return "";
    }

    @Override
    public String getPropertiesFileContent() {
        return null;
    }

    @Override
    public String getBuildGradleContentModule() {
        return DrivenAdapterTemplate.getBuildGradleMongoRepository();
    }

    @Override
    public String getSettingsGradleModule() {
        return DrivenAdapterTemplate
                .getSettingsMongoRepositoryContent().
                        concat(HelperTemplate.getSettingsHelperMongoContent());
    }

}
