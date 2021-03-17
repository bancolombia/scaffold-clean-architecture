package co.com.bancolombia.model.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static co.com.bancolombia.model.exception.Constants.*;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorMessage {

    AUTHENTICATION_SAVE("AST0009", "Error saving authentication", "439", A_SYSTEM_FAILURE_OCCURRED),
    AUTHENTICATION_FIND("AST0010", "Error finding authentication", "440", A_SYSTEM_FAILURE_OCCURRED),
    AUTHENTICATION_DELETE("AST0013", "Error deleting authentication", "443", A_SYSTEM_FAILURE_OCCURRED);

    private final String code;
    private final String description;
    private final String itcCode;
    private final String message;
}
