package com.atg.autonexo.backend.shared.infrastructure.web;

/**
 * Standardized success payload for endpoints that only need to return a
 * human-readable confirmation message (e.g. "User registered successfully").
 * <p>
 * Wrapping the message in a JSON object keeps response shapes consistent
 * across the API: the frontend can always parse the body as JSON.
 * </p>
 *
 * @param message user-friendly confirmation message
 */
public record MessageResponse(String message) {
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}
