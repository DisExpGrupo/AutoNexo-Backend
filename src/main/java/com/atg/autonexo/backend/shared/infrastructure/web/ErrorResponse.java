package com.atg.autonexo.backend.shared.infrastructure.web;

import java.time.LocalDateTime;

/**
 * Standardized error response payload returned by all error handlers.
 * <p>
 * Every error response in the API follows this JSON schema so the frontend
 * can rely on a single, predictable structure:
 * </p>
 * <pre>
 * {
 *   "timestamp": "2026-06-09T12:34:56.789",
 *   "status":    400,
 *   "error":     "Bad Request",
 *   "errorCode": "VALIDATION_ERROR",
 *   "message":   "Email must be a valid email address",
 *   "path":      "/api/v1/users/signup"
 * }
 * </pre>
 *
 * @param timestamp  when the error was produced
 * @param status     HTTP status code
 * @param error      short HTTP reason phrase
 * @param errorCode  machine-readable code (see {@link ErrorCode})
 * @param message    user-friendly, safe-to-display message
 * @param path       request URI that produced the error
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        ErrorCode errorCode,
        String message,
        String path
) {
    public static ErrorResponse of(int status, String error, ErrorCode errorCode, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, errorCode, message, path);
    }
}
