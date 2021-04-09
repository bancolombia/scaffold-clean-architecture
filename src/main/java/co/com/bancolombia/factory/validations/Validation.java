package co.com.bancolombia.factory.validations;

import co.com.bancolombia.exceptions.ValidationException;
import co.com.bancolombia.factory.ModuleBuilder;

public interface Validation {
  void validate(ModuleBuilder moduleBuilder) throws ValidationException;
}
