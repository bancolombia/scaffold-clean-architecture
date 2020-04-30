package co.com.bancolombia.models.adapters.drivenadapters;

import co.com.bancolombia.models.adapters.AbstractModule;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.DrivenAdapterTemplate;
import co.com.bancolombia.templates.HelperTemplate;
import co.com.bancolombia.templates.properties.PropertiesTemplate;

import java.io.IOException;

public class JPADrivenAdapter extends AbstractModule {

    public JPADrivenAdapter() throws IOException {
        super();
        super.setName("jpa-repository");
        super.setNameHelper("jpa-repository-commons");
        super.setModulePackage("jpa");
        super.setHelperPackage("jpa");
        super.setConfigFileName("JpaConfig");
        super.setPropertiesFileName("jpaAdapter");
        super.setModelName("Secret");
        super.setModelDir(Constants.DOMAIN.concat("/")
                .concat(Constants.MODEL).concat("/")
                .concat(Constants.MAIN_JAVA).concat("/")
                .concat(super.getPackageName()).concat("/")
                .concat(Constants.MODEL).concat("/")
                .concat(DrivenAdapterTemplate.SECRET_MODEL_NAME));
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
        return DrivenAdapterTemplate.JPA_REPOSITORY_CLASS;
    }

    @Override
    public String getModuleClassContent() {
        return DrivenAdapterTemplate
                .getJPARepositoryClassContent(super.getPackageName()
                        .concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getInterfaceNameModule() {
        return DrivenAdapterTemplate.JPA_REPOSITORY_INTERFACE;
    }

    @Override
    public String getModuleInterfaceContent() {
        return DrivenAdapterTemplate
                .getJPARepositoryInterfaceContent(super.getPackageName()
                        .concat(".").concat(super.getModulePackage()));
    }

    @Override
    public String getBuildGradleModule() {
        return HelperTemplate.getBuildGradleHelperJPARepository();
    }

    @Override
    public String getHelperModuleClassContent() {
        return HelperTemplate
                .getHelperJPARepositoryClassContent(super.getPackageName()
                        .concat(".").concat(super.getHelperPackage()));
    }

    @Override
    public String getAppServiceImports() {
        return "dependencies { \n".concat(DrivenAdapterTemplate.getJPAImportContent());
    }

    @Override
    public String getPropertiesFileContent() {
        return PropertiesTemplate.getJpaPropertiesContent();
    }

    @Override
    public String getBuildGradleContentModule() {
        return DrivenAdapterTemplate.getBuildGradleJPARepository();
    }

    @Override
    public String getSettingsGradleModule() {
        return DrivenAdapterTemplate
                .getSettingsJPARepositoryContent()
                .concat(HelperTemplate.getSettingsHelperJPAContent());
    }
}
