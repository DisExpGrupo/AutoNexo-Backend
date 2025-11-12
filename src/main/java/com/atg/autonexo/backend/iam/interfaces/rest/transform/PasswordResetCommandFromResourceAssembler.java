package com.atg.autonexo.backend.iam.interfaces.rest.transform;

import com.atg.autonexo.backend.iam.domain.model.commands.RequestPasswordResetCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.ResetPasswordCommand;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.RequestPasswordResetResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.ResetPasswordResource;

/**
 * Assemblers for converting password reset resources to commands.
 */
public class PasswordResetCommandFromResourceAssembler {
    
    public static RequestPasswordResetCommand toCommandFromResource(RequestPasswordResetResource resource) {
        return new RequestPasswordResetCommand(resource.email());
    }
    
    public static ResetPasswordCommand toCommandFromResource(ResetPasswordResource resource) {
        return new ResetPasswordCommand(resource.token(), resource.newPassword());
    }
}


