package co.com.bancolombia.model.exception;

import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessException extends Exception {

  private final BusinessErrorMessage businessErrorMessage;
}
