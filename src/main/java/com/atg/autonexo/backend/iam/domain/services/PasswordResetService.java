package com.atg.autonexo.backend.iam.domain.services;

import com.atg.autonexo.backend.iam.domain.model.commands.RequestPasswordResetCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.ResetPasswordCommand;

/**
 * Domain service interface for password reset operations.
 */
public interface PasswordResetService {
    
    /**
     * Requests a password reset by generating a token and sending it to the user's email.
     * @param command the command containing the user's email
     */
    void handle(RequestPasswordResetCommand command);
    
    /**
     * Resets the password using a valid reset token.
     * @param command the command containing the token and new password
     */
    void handle(ResetPasswordCommand command);
}


