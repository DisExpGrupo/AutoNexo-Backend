package com.atg.autonexo.backend.iam.interfaces.rest.resources;

import java.util.Date;
import java.util.List;

/**
 * Resource for user information responses
 * <p>
 * This record represents the data transfer object for user information responses.
 * It provides a clean API response structure without exposing sensitive information
 * like password hashes.
 * </p>
 */
public record UserResource(
    Long id,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    boolean isVerified,
    boolean active,
    List<String> roles,
    Long workshopId,
    Date createdAt,
    Date updatedAt
) {
    
}

