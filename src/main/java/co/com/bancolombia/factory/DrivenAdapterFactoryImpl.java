package co.com.bancolombia.factory;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.models.Module;
import co.com.bancolombia.models.drivenAdapters.JPADrivenAdapter;
import co.com.bancolombia.models.drivenAdapters.MongoDrivenAdapter;
import co.com.bancolombia.models.drivenAdapters.SecretManagerDrivenAdapter;

import java.io.IOException;

public class DrivenAdapterFactoryImpl implements ModuleFactory {

    @Override
    public Module makeDrivenAdapter(int codeDrivenAdapter) throws IOException {
        Module drivenAdapter = null;
        if (Constants.getNameDrivenAdapter(codeDrivenAdapter) == null) { throw new IllegalArgumentException("Entry Point not is available \"(1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer )"); }


        switch (codeDrivenAdapter){
            case 1:
                drivenAdapter = new JPADrivenAdapter();
                drivenAdapter.setName("jpa-repository");
                drivenAdapter.setNameHelper("jpa-repository-commons");
                drivenAdapter.setModulePackage("jpa");
                drivenAdapter.setHelperPackage("jpa");
                break;
            case 2:
                drivenAdapter = new MongoDrivenAdapter();
                drivenAdapter.setName("mongo-repository");
                drivenAdapter.setNameHelper("mongo-repository-commons");
                drivenAdapter.setModulePackage("mongo");
                drivenAdapter.setHelperPackage("mongo");
                break;
            case 3:
                drivenAdapter = new SecretManagerDrivenAdapter();
                drivenAdapter.setName("secrets-manager-consumer");
                drivenAdapter.setModulePackage("secrets");
                break;
            default:
                break;
        }
        drivenAdapter.setPackageName(Utils.readProperties("package"));
        drivenAdapter.setPackageName(drivenAdapter.getPackageName().replaceAll("\\.", "\\/"));
        drivenAdapter.setCode(codeDrivenAdapter);
        drivenAdapter.setModuleDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS).concat("/").concat(drivenAdapter.getName()));

        if(drivenAdapter.helperModuleExist()) {
            drivenAdapter.setHelperDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.HELPERS).concat("/").concat(drivenAdapter.getNameHelper()));
        }
        if(codeDrivenAdapter == 3){
            drivenAdapter.setModelDir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()));
        }
        return drivenAdapter;
    }
}
