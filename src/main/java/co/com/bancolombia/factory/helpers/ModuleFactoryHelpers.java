package co.com.bancolombia.factory.helpers;

import co.com.bancolombia.factory.ModuleFactory;

public class ModuleFactoryHelpers {

  private ModuleFactoryHelpers() {}

  public static ModuleFactory getDrivenAdapterFactory() {
    return new HelperGeneric();
  }
}
