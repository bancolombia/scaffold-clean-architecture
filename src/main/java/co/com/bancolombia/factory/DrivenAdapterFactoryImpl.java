package co.com.bancolombia.factory;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.Module;
import co.com.bancolombia.models.drivenadapters.AsyncEventBusDrivenAdapter;
import co.com.bancolombia.models.drivenadapters.JPADrivenAdapter;
import co.com.bancolombia.models.drivenadapters.MongoDrivenAdapter;
import co.com.bancolombia.models.drivenadapters.SecretManagerDrivenAdapter;
import co.com.bancolombia.templates.DrivenAdapterTemplate;

import java.io.IOException;

public class DrivenAdapterFactoryImpl implements ModuleFactory {

    @Override
    public Module makeDrivenAdapter(int codeDrivenAdapter) throws IOException, CleanException {
        Module drivenAdapter = null;
        if (DrivenAdapterTemplate.getNameDrivenAdapter(codeDrivenAdapter) == null) {
            throw new IllegalArgumentException("Entry Point not is available (1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer, 4 -> Async Event Bus )");
        }

        switch (codeDrivenAdapter) {
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
                drivenAdapter.setModelName(DrivenAdapterTemplate.SECRET_MANAGER_CONSUMER_CLASS);
                break;
            case 4:
                drivenAdapter = new AsyncEventBusDrivenAdapter();
                drivenAdapter.setName("async-event-bus");
                drivenAdapter.setModulePackage("events");
                drivenAdapter.setModelName(DrivenAdapterTemplate.EVENT_BUS_GATEWAY_CLASS);
                break;
            default:
                throw new CleanException("Driven Adapter invalid");
        }

        drivenAdapter.setPackageName(Utils.readProperties("package"));
        drivenAdapter.setPackageName(drivenAdapter.getPackageName().replaceAll("\\.", "\\/"));
        drivenAdapter.setCode(codeDrivenAdapter);
        drivenAdapter.setModuleDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS).concat("/").concat(drivenAdapter.getName()));

        if (drivenAdapter.helperModuleExist()) {
            drivenAdapter.setHelperDir(Constants.INFRASTRUCTURE.concat("/").concat(Constants.HELPERS).concat("/").concat(drivenAdapter.getNameHelper()));
        }
        if (codeDrivenAdapter == 3) {
            drivenAdapter.setModelDir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()));
        }
        if (codeDrivenAdapter == 4) {
            drivenAdapter.setModelDir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()));
        }

        return drivenAdapter;
    }
}
