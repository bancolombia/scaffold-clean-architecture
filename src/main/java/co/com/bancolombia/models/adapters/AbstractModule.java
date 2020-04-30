package co.com.bancolombia.models.adapters;

import co.com.bancolombia.Utils;
import co.com.bancolombia.templates.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public abstract class AbstractModule {

    private int code;
    private String packageName;
    private String name;
    private String nameHelper;
    private String modulePackage;
    private String helperPackage;
    private String moduleDir;
    private String helperDir;
    private String modelName;
    private String modelDir;
    private String configFileName;
    private String propertiesFileName;
    private String gatewayDir;
    private String gatewayName;

    public AbstractModule() throws IOException {
        setPackageName(Utils.readProperties("package").replaceAll("\\.", "\\/"));

    }

    public abstract String getClassNameModule();

    public abstract String getBuildGradleContentModule();

    public abstract String getSettingsGradleModule();

    public abstract String getModuleClassContent();

    public String getInterfaceNameModule() {
        return null;
    }

    public String getModuleInterfaceContent() {
        return null;
    }

    public String getBuildGradleModule() {
        return null;
    }

    public String getHelperModuleClassContent() {
        return null;
    }

    public String getInterfaceModule() {
        return null;
    }

    public boolean hasModelDir() {
        return modelDir != null;
    }

    public boolean hasHelperModule() {
        return nameHelper != null;
    }

    public boolean hasConfigFile() {
        return configFileName != null;
    }

    public boolean hasPropertiesFile() {
        return configFileName != null;
    }

    public boolean hasGateway() {
        return gatewayName != null;
    }

    public String getModuleDirSrc() {
        return moduleDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName)
                .concat("/").concat(modulePackage);
    }

    public String getModuleDirTest() {
        return moduleDir.concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName)
                .concat("/").concat(modulePackage);
    }

    public abstract String getAppServiceImports();

    public abstract String getPropertiesFileContent();

}
