package com.atg.autonexo.backend.iam.domain.services;

import java.util.List;
import java.util.Optional;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.queries.GetAllUsersQuery;
import com.atg.autonexo.backend.iam.domain.model.queries.GetCurrentUserQuery;
import com.atg.autonexo.backend.iam.domain.model.queries.GetUserByEmailQuery;

/**
 * Defines the contract for query-based (read-only) operations on the User aggregate.
 * Implementations are typically found in the Application layer.
 */
public interface UserQueryService {
    /**
     * Handles the query to find a user by email.
     * @param query The query containing the email.
     * @return An Optional containing the User aggregate if found.
     */
    Optional<User> handle(GetUserByEmailQuery query);

    /**
     * Handles the query to retrieve all users.
     * @param query The query (empty, used for consistency).
     * @return A list of all User aggregates.
     */
    List<User> handle(GetAllUsersQuery query);
    
    /**
     * Handles the query to get the current authenticated user.
     * @param query The query containing the user ID.
     * @return An Optional containing the User aggregate if found.
     */
    Optional<User> handle(GetCurrentUserQuery query);
}
