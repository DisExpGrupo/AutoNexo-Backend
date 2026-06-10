package com.atg.autonexo.backend.iam.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atg.autonexo.backend.iam.domain.model.exceptions.InvalidCredentialsException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.InvalidTokenException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UnauthorizedException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserAccountDeactivatedException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserAlreadyExistsException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserNotFoundException;
import com.atg.autonexo.backend.shared.infrastructure.web.ErrorCode;
import com.atg.autonexo.backend.shared.infrastructure.web.ErrorResponse;

/**
 * IAM-scoped exception handler.
 * <p>
 * Maps domain exceptions from the Identity & Access Management bounded
 * context to standardized {@link ErrorResponse} payloads. This advice
 * takes precedence over the global handler for the same exception types.
 * </p>
 */
@RestControllerAdvice(basePackages = "com.atg.autonexo.backend.iam.interfaces")
public class IamExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(IamExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, WebRequest request) {
        LOGGER.warn("User not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {
        LOGGER.warn("Invalid credentials attempt");
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS,
                ex.getMessage(), request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, WebRequest request) {
        LOGGER.warn("Registration conflict: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS, ex.getMessage(), request);
    }

    @ExceptionHandler(UserAccountDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleDeactivated(
            UserAccountDeactivatedException ex, WebRequest request) {
        LOGGER.warn("Deactivated account access attempt");
        return build(HttpStatus.FORBIDDEN, ErrorCode.ACCOUNT_DEACTIVATED, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        LOGGER.warn("Unauthorized request: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, WebRequest request) {
        LOGGER.warn("Invalid token: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TOKEN, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status, ErrorCode code, String message, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ErrorResponse body = ErrorResponse.of(
                status.value(), status.getReasonPhrase(), code, message, path);
        return new ResponseEntity<>(body, status);
    }
}
