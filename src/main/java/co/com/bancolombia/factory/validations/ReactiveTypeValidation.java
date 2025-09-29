package co.com.bancolombia.factory.validations;

import co.com.bancolombia.exceptions.ValidationException;
import co.com.bancolombia.factory.ModuleBuilder;

public class ReactiveTypeValidation implements Validation {

  @Override
  public void validate(ModuleBuilder moduleBuilder) throws ValidationException {
    if (!moduleBuilder.isReactive()) {
      throw new ValidationException("This module is only available for reactive projects");
    }
  }
}
