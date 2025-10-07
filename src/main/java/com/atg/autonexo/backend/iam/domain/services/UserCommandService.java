package com.atg.autonexo.backend.iam.domain.services;

import com.atg.autonexo.backend.iam.domain.model.commands.SignInCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.SignUpCommand;

public interface UserCommandService {
    void handle(SignUpCommand command);

    void handle(SignInCommand command);
}
