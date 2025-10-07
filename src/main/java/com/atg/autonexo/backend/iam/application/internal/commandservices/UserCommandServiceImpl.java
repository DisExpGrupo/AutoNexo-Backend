package com.atg.autonexo.backend.iam.application.internal.commandservices;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.application.internal.outboundservices.hashing.HashingService;
import com.atg.autonexo.backend.iam.application.internal.outboundservices.tokens.TokenService;
import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.commands.SignInCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.SignUpCommand;
import com.atg.autonexo.backend.iam.domain.model.entities.Role;
import com.atg.autonexo.backend.iam.domain.model.exceptions.InvalidCredentialsException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserAccountDeactivatedException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserAlreadyExistsException;
import com.atg.autonexo.backend.iam.domain.services.RoleValidationService;
import com.atg.autonexo.backend.iam.domain.services.UserCommandService;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

/**
 * User Command Service Implementation
 * <p>
 * This service handles command-based operations for the User aggregate.
 * It implements the UserCommandService interface and provides business logic
 * for user registration and authentication.
 * </p>
 */
@Service
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCommandServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleValidationService roleValidationService;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleValidationService roleValidationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleValidationService = roleValidationService;
    }

    @Override
    public void handle(SignUpCommand command) {
        LOGGER.info("Processing SignUp command for email: {}", command.email());

        // Check if user already exists
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        // Validate requested role
        if (!roleValidationService.canRequestRole(command.requestedRole())) {
            throw new IllegalArgumentException("Cannot request role: " + command.requestedRole());
        }

        // Hash the password
        String hashedPassword = hashingService.encode(command.password());

        // Create new user
        User user = new User(
                command.email(),
                hashedPassword,
                command.firstName(),
                command.lastName(),
                command.phoneNumber(),
                false // Not verified by default
        );

        // Assign requested role
        Role requestedRole = roleRepository.findByName(command.requestedRole())
                .orElseThrow(() -> new IllegalStateException("Requested role " + command.requestedRole() + " not found"));
        
        user.addRole(requestedRole);

        // Save user
        User savedUser = userRepository.save(user);
        LOGGER.info("User registered successfully with ID: {}", savedUser.getId());
    }

    @Override
    public void handle(SignInCommand command) {
        LOGGER.info("Processing SignIn command for email: {}", command.email());

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(command.email());
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = userOptional.get();

        // Check if user is active
        if (!user.getActive()) {
            throw new UserAccountDeactivatedException(command.email());
        }

        // Verify password
        if (!hashingService.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        LOGGER.info("User authenticated successfully with ID: {}", user.getId());
    }

    /**
     * Generate JWT token for authenticated user
     * @param user the authenticated user
     * @return JWT token string
     */
    public String generateTokenForUser(User user) {
        String userRole = user.getRoles().isEmpty() ? "CAR_OWNER" : 
                         user.getRoles().get(0).getName().name();
        
        Long workshopId = user.getWorkshop() != null ? user.getWorkshop().getId() : null;
        
        return tokenService.generateToken(user.getId(), userRole, workshopId);
    }
}
