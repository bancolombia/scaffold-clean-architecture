package co.com.bancolombia.model.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static co.com.bancolombia.model.exception.Constants.*;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessage {

    AUTHENTICATION_NOT_FOUND("ASB0011", "Authentication not found", "471", A_SYSTEM_FAILURE_OCCURRED);

    private final String code;
    private final String description;
    private final String itcCode;
    private final String message;
}