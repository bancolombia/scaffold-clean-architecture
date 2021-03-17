package co.com.bancolombia.model.exception;

import co.com.bancolombia.model.exception.message.TechnicalErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TechnicalException extends Exception {

    private final TechnicalErrorMessage technicalErrorMessage;

    public TechnicalException(Throwable cause, TechnicalErrorMessage technicalErrorMessage) {
        super(cause);
        this.technicalErrorMessage = technicalErrorMessage;
    }
}