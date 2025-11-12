package com.atg.autonexo.backend.iam.interfaces.rest.transform;

import com.atg.autonexo.backend.iam.domain.model.commands.ChangePasswordCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.UpdateUserProfileCommand;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.ChangePasswordResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.UpdateUserProfileResource;

/**
 * Assemblers for converting user profile resources to commands.
 */
public class UserProfileCommandFromResourceAssembler {
    
    public static UpdateUserProfileCommand toCommandFromResource(Long userId, UpdateUserProfileResource resource) {
        return new UpdateUserProfileCommand(
            userId,
            resource.firstName(),
            resource.lastName(),
            resource.phoneNumber()
        );
    }
    
    public static ChangePasswordCommand toCommandFromResource(Long userId, ChangePasswordResource resource) {
        return new ChangePasswordCommand(
            userId,
            resource.currentPassword(),
            resource.newPassword()
        );
    }
}


