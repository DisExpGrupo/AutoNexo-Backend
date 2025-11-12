package com.atg.autonexo.backend.iam.domain.services;

import com.atg.autonexo.backend.iam.domain.model.commands.ChangePasswordCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.DeactivateUserCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.SignInCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.SignUpCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.UpdateUserProfileCommand;

public interface UserCommandService {
    void handle(SignUpCommand command);

    void handle(SignInCommand command);
    
    void handle(UpdateUserProfileCommand command);
    
    void handle(ChangePasswordCommand command);
    
    void handle(DeactivateUserCommand command);
}
