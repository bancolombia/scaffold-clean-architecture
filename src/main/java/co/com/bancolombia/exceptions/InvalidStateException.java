package co.com.bancolombia.exceptions;

public class InvalidStateException extends CleanUncheckedException {
  public InvalidStateException(String message) {
    super(message);
  }
}
