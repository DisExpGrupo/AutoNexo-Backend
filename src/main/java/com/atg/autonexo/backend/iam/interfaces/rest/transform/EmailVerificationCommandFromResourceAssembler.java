package com.atg.autonexo.backend.iam.interfaces.rest.transform;

import com.atg.autonexo.backend.iam.domain.model.commands.ResendVerificationCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.VerifyEmailCommand;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.ResendVerificationResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.VerifyEmailResource;

/**
 * Assemblers for converting email verification resources to commands.
 */
public class EmailVerificationCommandFromResourceAssembler {
    
    public static ResendVerificationCommand toCommandFromResource(ResendVerificationResource resource) {
        return new ResendVerificationCommand(resource.email());
    }
    
    public static VerifyEmailCommand toCommandFromResource(VerifyEmailResource resource) {
        return new VerifyEmailCommand(resource.token());
    }
}


