package com.atg.autonexo.backend.iam.interfaces.rest.transform;

import java.util.List;
import java.util.stream.Collectors;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.interfaces.rest.resources.UserResource;

/**
 * Assembler for converting User entity to UserResource
 * <p>
 * This class provides static methods to transform data between the domain layer
 * (entities) and the interface layer (REST resources) following DDD principles.
 * </p>
 */
public class UserResourceFromEntityAssembler {
    
    /**
     * Converts a User entity to a UserResource
     * @param user the User entity from the domain
     * @return UserResource for REST response
     */
    public static UserResource toResourceFromEntity(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        
        Long workshopId = user.getWorkshop() != null ? user.getWorkshop().getId() : null;
        
        return new UserResource(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.isVerified(),
            user.getActive(),
            roleNames,
            workshopId,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}

