package com.atg.autonexo.backend.iam.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.queries.GetAllUsersQuery;
import com.atg.autonexo.backend.iam.domain.model.queries.GetUserByEmailQuery;
import com.atg.autonexo.backend.iam.domain.services.UserQueryService;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

/**
 * User Query Service Implementation
 * <p>
 * This service handles query-based (read-only) operations for the User aggregate.
 * It implements the UserQueryService interface and provides business logic
 * for user retrieval operations.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserQueryServiceImpl.class);

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        LOGGER.debug("Processing GetUserByEmailQuery for email: {}", query.email());
        
        Optional<User> user = userRepository.findByEmail(query.email());
        
        if (user.isPresent()) {
            LOGGER.debug("User found with ID: {}", user.get().getId());
        } else {
            LOGGER.debug("No user found with email: {}", query.email());
        }
        
        return user;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        LOGGER.debug("Processing GetAllUsersQuery");
        
        List<User> users = userRepository.findAll();
        
        LOGGER.debug("Retrieved {} users", users.size());
        
        return users;
    }
}
