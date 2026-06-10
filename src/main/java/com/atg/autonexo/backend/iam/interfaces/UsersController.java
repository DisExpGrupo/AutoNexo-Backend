package com.atg.autonexo.backend.iam.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.iam.application.internal.commandservices.UserCommandServiceImpl;
import com.atg.autonexo.backend.iam.application.internal.queryservices.UserQueryServiceImpl;
import com.atg.autonexo.backend.iam.domain.model.commands.ChangePasswordCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.DeactivateUserCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.SignInCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.SignUpCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.UpdateUserProfileCommand;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UnauthorizedException;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserNotFoundException;
import com.atg.autonexo.backend.iam.domain.model.queries.GetAllUsersQuery;
import com.atg.autonexo.backend.iam.domain.model.queries.GetCurrentUserQuery;
import com.atg.autonexo.backend.iam.domain.model.queries.GetUserByEmailQuery;
import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.services.EmailVerificationService;
import com.atg.autonexo.backend.iam.domain.services.PasswordResetService;
import com.atg.autonexo.backend.iam.domain.services.RoleValidationService;
import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.AuthenticationResponseResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.RequestPasswordResetResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.ResendVerificationResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.ResetPasswordResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.SignInResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.SignUpResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.UpdateUserProfileResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.UserResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.VerifyEmailResource;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.ChangePasswordResource;
import com.atg.autonexo.backend.iam.interfaces.rest.transform.EmailVerificationCommandFromResourceAssembler;
import com.atg.autonexo.backend.iam.interfaces.rest.transform.PasswordResetCommandFromResourceAssembler;
import com.atg.autonexo.backend.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.atg.autonexo.backend.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.atg.autonexo.backend.iam.interfaces.rest.transform.UserProfileCommandFromResourceAssembler;
import com.atg.autonexo.backend.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.atg.autonexo.backend.shared.infrastructure.web.MessageResponse;

import jakarta.validation.Valid;

/**
 * Users REST Controller
 * <p>
 * Handles HTTP requests for user-related operations: registration,
 * authentication, profile management, password reset, and email
 * verification. The controller focuses on the happy path; all error
 * handling is delegated to {@code IamExceptionHandler} and the global
 * {@code GlobalExceptionHandler}.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);
    private static final String FORGOT_PASSWORD_RESPONSE =
            "If an account exists with this email, a password reset link has been sent.";

    private final UserCommandServiceImpl userCommandService;
    private final UserQueryServiceImpl userQueryService;
    private final RoleValidationService roleValidationService;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;

    public UsersController(
            UserCommandServiceImpl userCommandService,
            UserQueryServiceImpl userQueryService,
            RoleValidationService roleValidationService,
            PasswordResetService passwordResetService,
            EmailVerificationService emailVerificationService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.roleValidationService = roleValidationService;
        this.passwordResetService = passwordResetService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        LOGGER.info("Processing signup request for email: {}", signUpResource.email());

        SignUpCommand command = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        userCommandService.handle(command);

        LOGGER.info("User registered successfully: {}", signUpResource.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageResponse.of("User registered successfully"));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponseResource> signIn(@Valid @RequestBody SignInResource signInResource) {
        LOGGER.info("Processing signin request for email: {}", signInResource.email());

        SignInCommand command = SignInCommandFromResourceAssembler.toCommandfromResource(signInResource);
        userCommandService.handle(command);

        User user = userQueryService.handle(new GetUserByEmailQuery(signInResource.email()))
                .orElseThrow(() -> new UserNotFoundException());

        String token = userCommandService.generateTokenForUser(user);
        UserResource userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

        AuthenticationResponseResource response =
                AuthenticationResponseResource.of(token, 604800L, userResource);

        LOGGER.info("User authenticated successfully: {}", signInResource.email());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResource> getUserByEmail(@RequestParam String email) {
        LOGGER.debug("Processing getUserByEmail request for email: {}", email);

        User user = userQueryService.handle(new GetUserByEmailQuery(email))
                .orElseThrow(() -> new UserNotFoundException());

        return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResource>> getAllUsers() {
        LOGGER.debug("Processing getAllUsers request");

        List<UserResource> userResources = userQueryService.handle(new GetAllUsersQuery()).stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(userResources);
    }

    @GetMapping("/available-roles")
    public ResponseEntity<?> getAvailableRoles() {
        LOGGER.debug("Processing getAvailableRoles request");
        return ResponseEntity.ok(roleValidationService.getAvailableRolesForRegistration());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody RequestPasswordResetResource resource) {
        LOGGER.info("Processing forgot password request for email: {}", resource.email());

        // Silently return the same response regardless of outcome to prevent email enumeration.
        // Real errors (e.g. DB unavailable) will still surface as 500 via the global handler.
        try {
            var command = PasswordResetCommandFromResourceAssembler.toCommandFromResource(resource);
            passwordResetService.handle(command);
        } catch (com.atg.autonexo.backend.iam.domain.model.exceptions.UserNotFoundException
                 | com.atg.autonexo.backend.iam.domain.model.exceptions.UserAccountDeactivatedException e) {
            LOGGER.info("Forgot password: no action for email {} ({})",
                    resource.email(), e.getClass().getSimpleName());
        }

        LOGGER.info("Forgot password response sent for email: {}", resource.email());
        return ResponseEntity.ok(MessageResponse.of(FORGOT_PASSWORD_RESPONSE));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordResource resource) {
        LOGGER.info("Processing reset password request");

        var command = PasswordResetCommandFromResourceAssembler.toCommandFromResource(resource);
        passwordResetService.handle(command);

        LOGGER.info("Password reset successfully completed");
        return ResponseEntity.ok(MessageResponse.of("Password has been reset successfully"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody ResendVerificationResource resource) {
        LOGGER.info("Processing resend verification request for email: {}", resource.email());

        var command = EmailVerificationCommandFromResourceAssembler.toCommandFromResource(resource);
        emailVerificationService.handle(command);

        LOGGER.info("Verification email resent successfully");
        return ResponseEntity.ok(MessageResponse.of("Verification email has been sent"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody VerifyEmailResource resource) {
        LOGGER.info("Processing verify email request");

        var command = EmailVerificationCommandFromResourceAssembler.toCommandFromResource(resource);
        emailVerificationService.handle(command);

        LOGGER.info("Email verified successfully");
        return ResponseEntity.ok(MessageResponse.of("Email has been verified successfully"));
    }

    @GetMapping("/verification-status")
    public ResponseEntity<Map<String, Object>> getVerificationStatus(@RequestParam String email) {
        LOGGER.debug("Processing get verification status request for email: {}", email);

        User user = userQueryService.handle(new GetUserByEmailQuery(email))
                .orElseThrow(() -> new UserNotFoundException());

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "verified", user.isVerified()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResource> getCurrentUser() {
        Long userId = getCurrentUserId();
        LOGGER.debug("Processing get current user request for ID: {}", userId);

        User user = userQueryService.handle(new GetCurrentUserQuery(userId))
                .orElseThrow(() -> new UserNotFoundException());

        return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResource> updateCurrentUser(@Valid @RequestBody UpdateUserProfileResource resource) {
        Long userId = getCurrentUserId();
        LOGGER.info("Processing update profile request for user ID: {}", userId);

        UpdateUserProfileCommand command = UserProfileCommandFromResourceAssembler
                .toCommandFromResource(userId, resource);
        userCommandService.handle(command);

        User user = userQueryService.handle(new GetCurrentUserQuery(userId))
                .orElseThrow(() -> new UserNotFoundException());

        LOGGER.info("User profile updated successfully: {}", userId);
        return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user));
    }

    @PutMapping("/me/password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordResource resource) {
        Long userId = getCurrentUserId();
        LOGGER.info("Processing change password request for user ID: {}", userId);

        ChangePasswordCommand command = UserProfileCommandFromResourceAssembler
                .toCommandFromResource(userId, resource);
        userCommandService.handle(command);

        LOGGER.info("Password changed successfully for user: {}", userId);
        return ResponseEntity.ok(MessageResponse.of("Password has been changed successfully"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponse> deactivateCurrentUser() {
        Long userId = getCurrentUserId();
        LOGGER.info("Processing deactivate account request for user ID: {}", userId);

        userCommandService.handle(new DeactivateUserCommand(userId));

        LOGGER.info("User account deactivated successfully: {}", userId);
        return ResponseEntity.ok(MessageResponse.of("Account has been deactivated successfully"));
    }

    /**
     * Extracts the current authenticated user ID from the SecurityContext.
     *
     * @return the current user ID
     * @throws UnauthorizedException if the user is not authenticated
     */
    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException();
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw new UnauthorizedException();
    }
}
