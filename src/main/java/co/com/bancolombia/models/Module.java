package co.com.bancolombia.models;

import co.com.bancolombia.templates.Constants;
import lombok.Data;

@Data
public abstract class Module {
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

    public abstract String getClassNameModule();

    public abstract String getModuleClassContent();

    public abstract String getInterfaceNameModule();

    public abstract String getModuleInterfaceContent();

    public abstract String getBuildGradleModule();

    public abstract String getHelperModuleClassContent();

    public abstract String getBuildGradleContentModule();

    public abstract String getSettingsGradleModule();

    public abstract String getInterfaceModule();

    public boolean modelDirExist() {
        return modelDir != null;
    }

    public boolean helperModuleExist() {
        return nameHelper != null;
    }

    public String getModuleDirSrc(){ return moduleDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(modulePackage); }
    public String getModuleDirTest(){ return moduleDir.concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(modulePackage); }
}
