package co.com.bancolombia.factory;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.models.DrivenAdapter;
import co.com.bancolombia.models.JPADrivenAdapter;
import co.com.bancolombia.models.MongoDrivenAdapter;
import co.com.bancolombia.models.SecretManagerDrivenAdapter;

import java.io.IOException;

public class DrivenAdapterFactoryImpl implements ModuleFactory {

    @Override
    public DrivenAdapter makeDrivenAdapter(int codeDrivenAdapter) throws IOException {
        DrivenAdapter drivenAdapter = null;
        if (Constants.getNameDrivenAdapter(codeDrivenAdapter) == null) { throw new IllegalArgumentException("Entry Point not is available \"(1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer )"); }


        switch (codeDrivenAdapter){
            case 1:
                drivenAdapter = new JPADrivenAdapter();
                drivenAdapter.setNameDrivenAdapter("jpa-repository");
                drivenAdapter.setHelperDrivenAdapter("jpa-repository-commons");
                drivenAdapter.setDrivenAdapterPackage("jpa");
                drivenAdapter.setHelperDrivenAdapterPackage("jpa");
                break;
            case 2:
                drivenAdapter = new MongoDrivenAdapter();
                drivenAdapter.setNameDrivenAdapter("mongo-repository");
                drivenAdapter.setHelperDrivenAdapter("mongo-repository-commons");
                drivenAdapter.setDrivenAdapterPackage("mongo");
                drivenAdapter.setHelperDrivenAdapterPackage("mongo");
                break;
            case 3:
                drivenAdapter = new SecretManagerDrivenAdapter();
                drivenAdapter.setNameDrivenAdapter("secrets-manager-consumer");
                drivenAdapter.setDrivenAdapterPackage("secrets");
                break;
            default:
                break;
        }
        drivenAdapter.setPackageName(Utils.readProperties("package"));
        drivenAdapter.setPackageName(drivenAdapter.getPackageName().replaceAll("\\.", "\\/"));
        drivenAdapter.setNumberDrivenAdapter(codeDrivenAdapter);
        drivenAdapter.setDrivenAdapterDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS).concat("/").concat(drivenAdapter.getNameDrivenAdapter()));

        if(drivenAdapter.helperDrivenAdapterExist()) {
            drivenAdapter.setHelperDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.HELPERS).concat("/").concat(drivenAdapter.getHelperDrivenAdapter()));
        }
        if(codeDrivenAdapter == 3){
            drivenAdapter.setModelDir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()));
        }
        return drivenAdapter;
    }
}
