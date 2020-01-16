package co.com.bancolombia.models;

import co.com.bancolombia.Constants;
import lombok.Data;

@Data
public class DrivenAdapter {
    private int numberDrivenAdapter;
    private String packageName;
    private String nameDrivenAdapter;
    private String helperDrivenAdapter;
    private String drivenAdapterPackage;
    private String helperDrivenAdapterPackage;
    private String drivenAdapterDir;
    private String helperDir;
    private String modelDir;

    public String getClassNameEntryPoint(){

        switch (numberDrivenAdapter){
            case 1:
                return Constants.JPA_REPOSITORY_CLASS;
            case 2:
                return Constants.MONGO_REPOSITORY_CLASS;
            case 3:
                return Constants.SECRET_MANAGER_CLASS;
            default:
                return null;
        }
    }


    public String getDrivenAdapterClassContent(){

        switch (numberDrivenAdapter){
            case 1:
                return Constants.getJPARepositoryClassContent(packageName.concat(".").concat(drivenAdapterPackage));
            case 2:
                return Constants.getMongoRepositoryClassContent(packageName.concat(".").concat(drivenAdapterPackage));
            case 3:
                return Constants.getSecretsManagerClassContent(packageName, drivenAdapterPackage);
            default:
                return null;
        }
    }

    public String getInterfaceNameEntryPoint(){

        switch (numberDrivenAdapter){
            case 1:
                return Constants.JPA_REPOSITORY_INTERFACE;
            case 2:
                return Constants.MONGO_REPOSITORY_INTERFACE;
            case 3:
                return null;
            default:
                return null;
        }
    }

    public String getDrivenAdapterInterfaceContent(){

        switch (numberDrivenAdapter){
            case 1:
                return Constants.getJPARepositoryInterfaceContent(packageName.concat(".").concat(drivenAdapterPackage));
            case 2:
                return Constants.getMongoRepositoryInterfaceContent(packageName.concat(".").concat(drivenAdapterPackage));
            case 3:
                return null;
            default:
                return null;
        }
    }

    public String getBuildGradleDrivenAdapter(){

        switch (numberDrivenAdapter){
            case 1:
                return  Constants.getBuildGradleHelperJPARepository();
            case 2:
                return  Constants.getBuildGradleHelperMongoRepository();
            case 3:
                return null;
            default:
                return null;
        }
    }
    public String getHelperDrivenAdapterClassContent(){

        switch (numberDrivenAdapter){
            case 1:
                return  Constants.getHelperJPARepositoryClassContent(packageName.concat(".").concat(helperDrivenAdapterPackage));
            case 2:
                return  Constants.getHelperMongoRepositoryClassContent(packageName.concat(".").concat(helperDrivenAdapterPackage));
            case 3:
                return null;
            default:
                return null;
        }
    }

    public String getBuildGradleContentEntryPoint(){

        switch (numberDrivenAdapter){
            case 1:
                return Constants.getBuildGradleJPARepository();
            case 2:
                return Constants.getBuildGradleMongoRepository();
            case 3:
                return Constants.getBuildGradleSecretsManager();
            default:
                return null;
        }
    }

    public String getSettingsGradleDrivenAdapter(){
        String value = null;
        if (numberDrivenAdapter == 1) value = Constants.getSettingsJPARepositoryContent().concat(Constants.getSettingsHelperJPAContent());
        else if (numberDrivenAdapter == 2) value = Constants.getSettingsMongoRepositoryContent().concat(Constants.getSettingsHelperMongoContent());
        else if (numberDrivenAdapter == 3) value = Constants.getSettingsSecretsManagerContent();
        return value;
    }

    public boolean modelDirExist() {
        return modelDir != null;
    }
    public boolean helperDrivenAdapterExist() {
        return helperDrivenAdapter != null;
    }

    public String getDrivenAdapterDirSrc(){
       return drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapterPackage);

    }
}
