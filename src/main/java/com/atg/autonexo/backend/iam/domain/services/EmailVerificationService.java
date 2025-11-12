package com.atg.autonexo.backend.iam.domain.services;

import com.atg.autonexo.backend.iam.domain.model.commands.ResendVerificationCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.VerifyEmailCommand;

/**
 * Domain service interface for email verification operations.
 */
public interface EmailVerificationService {
    
    /**
     * Resends an email verification token to the user.
     * @param command the command containing the user's email
     */
    void handle(ResendVerificationCommand command);
    
    /**
     * Verifies the user's email using a verification token.
     * @param command the command containing the verification token
     */
    void handle(VerifyEmailCommand command);
}


