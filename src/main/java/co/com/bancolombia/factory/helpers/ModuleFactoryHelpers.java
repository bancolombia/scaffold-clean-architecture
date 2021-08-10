package co.com.bancolombia.factory.helpers;

import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.adapters.*;

public class ModuleFactoryHelpers {

  private ModuleFactoryHelpers() {}

  public static ModuleFactory getDrivenAdapterFactory() {
    return new HelperGeneric();
  }
}
