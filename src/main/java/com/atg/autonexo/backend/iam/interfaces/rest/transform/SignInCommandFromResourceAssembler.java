package com.atg.autonexo.backend.iam.interfaces.rest.transform;

import com.atg.autonexo.backend.iam.domain.model.commands.SignInCommand;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandfromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}
