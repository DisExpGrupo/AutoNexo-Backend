package com.atg.autonexo.backend.workshop.domain.services;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.commands.*;
import com.atg.autonexo.backend.workshop.domain.model.entities.Location;
import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;

/**
 * Domain service interface for Workshop command operations.
 * Follows CQRS pattern for write operations.
 */
public interface WorkshopCommandService {
    
    /**
     * Creates a new workshop
     */
    Workshop handle(CreateWorkshopCommand command);
    
    /**
     * Updates workshop basic information
     */
    Workshop handle(UpdateWorkshopCommand command);
    
    /**
     * Adds a location to a workshop
     */
    Location handle(AddLocationCommand command);
    
    /**
     * Adds a staff member to a workshop
     */
    StaffMember handle(AddStaffMemberCommand command);
    
    /**
     * Adds a service template to a workshop
     */
    ServiceTemplate handle(AddServiceTemplateCommand command);
    
    /**
     * Updates a service template
     */
    ServiceTemplate handle(UpdateServiceTemplateCommand command);
    
    /**
     * Adds a capability tag to a workshop
     */
    void handle(AddCapabilityTagCommand command);
    
    /**
     * Updates workshop capability tags (replaces all existing tags)
     */
    void handle(UpdateWorkshopCapabilityTagsCommand command);
    
    /**
     * Updates workshop subscription
     */
    Workshop handle(UpdateSubscriptionCommand command);
    
    /**
     * Updates a location
     */
    Location handle(UpdateLocationCommand command);
    
    /**
     * Deletes/deactivates a location
     */
    void handle(DeleteLocationCommand command);
}
