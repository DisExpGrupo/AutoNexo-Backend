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
import com.atg.autonexo.backend.iam.domain.model.valueobjects.Roles;
import com.atg.autonexo.backend.iam.domain.services.RoleValidationService;
import com.atg.autonexo.backend.iam.domain.services.UserCommandService;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.workshop.interfaces.acl.WorkshopContextFacade;

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
    private final WorkshopContextFacade workshopContextFacade;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleValidationService roleValidationService,
            WorkshopContextFacade workshopContextFacade) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleValidationService = roleValidationService;
        this.workshopContextFacade = workshopContextFacade;
    }

    @Override
    public void handle(SignUpCommand command) {
        LOGGER.info("Processing SignUp command for email: {} with role: {}", 
            command.email(), command.requestedRole());

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
        
        // Process invitation if user is WORKSHOP_EMPLOYEE and has invitation code
        if (command.requestedRole() == Roles.WORKSHOP_EMPLOYEE && 
            command.invitationCode() != null && !command.invitationCode().isBlank()) {
            
            LOGGER.info("Processing invitation code for WORKSHOP_EMPLOYEE: {}", command.invitationCode());
            
            try {
                Long workshopId = workshopContextFacade.processInvitationForNewUser(
                    command.invitationCode(), 
                    command.email(), 
                    savedUser.getId()
                );
                
                LOGGER.info("User {} successfully added to workshop {} via invitation", 
                    savedUser.getId(), workshopId);
                
            } catch (IllegalArgumentException | IllegalStateException e) {
                // If invitation processing fails, delete the created user to maintain consistency
                LOGGER.error("Failed to process invitation for user {}. Rolling back user creation.", 
                    savedUser.getId());
                userRepository.delete(savedUser);
                throw new IllegalArgumentException(
                    "Failed to process invitation: " + e.getMessage(), e);
            }
        }
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
        
        Long workshopId = user.getWorkshopReference() != null ? user.getWorkshopReference().getWorkshopId().id() : null;
        
        return tokenService.generateToken(user.getId(), userRole, workshopId);
    }
}
