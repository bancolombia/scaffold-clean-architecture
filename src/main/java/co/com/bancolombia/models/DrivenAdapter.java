package co.com.bancolombia.models;

import co.com.bancolombia.Constants;
import lombok.Data;

@Data
public abstract class DrivenAdapter {
    private int numberDrivenAdapter;
    private String packageName;
    private String nameDrivenAdapter;
    private String helperDrivenAdapter;
    private String drivenAdapterPackage;
    private String helperDrivenAdapterPackage;
    private String drivenAdapterDir;
    private String helperDir;
    private String modelDir;

    public abstract String getClassNameEntryPoint();

    public abstract String getDrivenAdapterClassContent();

    public abstract String getInterfaceNameEntryPoint();

    public abstract String getDrivenAdapterInterfaceContent();

    public abstract String getBuildGradleDrivenAdapter();

    public abstract String getHelperDrivenAdapterClassContent();

    public abstract String getBuildGradleContentDrivenAdapter();

    public abstract String getSettingsGradleDrivenAdapter();

    public boolean modelDirExist() {
        return modelDir != null;
    }

    public boolean helperDrivenAdapterExist() {
        return helperDrivenAdapter != null;
    }

    public String getDrivenAdapterDirSrc(){ return drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapterPackage); }
}
