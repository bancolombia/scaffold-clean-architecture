package co.com.bancolombia.exceptions;

public class CleanDomainException extends CleanUncheckedException {
  public CleanDomainException(String message) {
    super(message);
  }
}
