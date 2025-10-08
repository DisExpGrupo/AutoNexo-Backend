package com.atg.autonexo.backend.workshop.application.internal.commandservices;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Address;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopAlreadyExistsException;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.commands.*;
import com.atg.autonexo.backend.workshop.domain.model.entities.Location;
import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.BusinessRegistration;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.ServiceTemplateCode;
import com.atg.autonexo.backend.workshop.domain.services.WorkshopCommandService;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of Workshop Command Service.
 * Handles all write operations for the Workshop aggregate.
 */
@Service
@Transactional
public class WorkshopCommandServiceImpl implements WorkshopCommandService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkshopCommandServiceImpl.class);
    
    private final WorkshopRepository workshopRepository;
    
    public WorkshopCommandServiceImpl(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }
    
    @Override
    public Workshop handle(CreateWorkshopCommand command) {
        LOGGER.info("Creating workshop for owner user ID: {}", command.ownerUserId());
        
        try {
            // Check if workshop already exists for this owner
            if (workshopRepository.existsByOwnerUserId(command.ownerUserId())) {
                throw new WorkshopAlreadyExistsException(command.ownerUserId());
            }
            
            // Create business registration if RUC is provided
            BusinessRegistration businessRegistration = null;
            if (command.ruc() != null && !command.ruc().isBlank()) {
                businessRegistration = BusinessRegistration.unverified(command.ruc());
            }
            
            // Create workshop
            Workshop workshop = new Workshop(
                new UserId(command.ownerUserId()),
                command.name(),
                command.shortDescription(),
                command.legalName(),
                businessRegistration
            );
            
            Workshop savedWorkshop = workshopRepository.save(workshop);
            LOGGER.info("Workshop created successfully with ID: {}", savedWorkshop.getId());
            
            return savedWorkshop;
            
        } catch (WorkshopAlreadyExistsException e) {
            LOGGER.error("Workshop already exists for user ID: {}", command.ownerUserId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error creating workshop: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create workshop", e);
        }
    }
    
    @Override
    public Workshop handle(UpdateWorkshopCommand command) {
        LOGGER.info("Updating workshop ID: {}", command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            workshop.updateBasicInfo(
                command.name(),
                command.shortDescription(),
                command.legalName()
            );
            
            Workshop updatedWorkshop = workshopRepository.save(workshop);
            LOGGER.info("Workshop updated successfully: {}", command.workshopId());
            
            return updatedWorkshop;
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating workshop: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update workshop", e);
        }
    }
    
    @Override
    public Location handle(AddLocationCommand command) {
        LOGGER.info("Adding location to workshop ID: {}", command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            Address address = new Address(
                command.street(),
                command.city(),
                command.state(),
                command.zip(),
                command.country()
            );
            
            Coordinates coordinates = null;
            if (command.latitude() != null && command.longitude() != null) {
                coordinates = new Coordinates(command.latitude(), command.longitude());
            }
            
            Location location = new Location(address, coordinates);
            workshop.addLocation(location);
            
            workshopRepository.save(workshop);
            LOGGER.info("Location added successfully to workshop: {}", command.workshopId());
            
            return location;
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding location: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add location", e);
        }
    }
    
    @Override
    public StaffMember handle(AddStaffMemberCommand command) {
        LOGGER.info("Adding staff member to workshop ID: {}", command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            StaffMember staffMember = new StaffMember(
                new UserId(command.userId()),
                command.primaryLocationId()
            );
            
            workshop.addStaffMember(staffMember);
            workshopRepository.save(workshop);
            
            LOGGER.info("Staff member added successfully to workshop: {}", command.workshopId());
            return staffMember;
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding staff member: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add staff member", e);
        }
    }
    
    @Override
    public ServiceTemplate handle(AddServiceTemplateCommand command) {
        LOGGER.info("Adding service template to workshop ID: {}", command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            ServiceTemplateCode code = null;
            if (command.code() != null && !command.code().isBlank()) {
                code = new ServiceTemplateCode(command.code());
            }
            
            Money basePrice = null;
            if (command.basePriceAmount() != null && command.currency() != null) {
                basePrice = new Money(command.basePriceAmount(), command.currency());
            }
            
            // Parse catalog service (optional - can be null for custom services)
            com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog catalogService = null;
            if (command.catalogService() != null && !command.catalogService().isBlank()) {
                try {
                    catalogService = com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog
                        .fromString(command.catalogService());
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid catalog service: {}", command.catalogService());
                    throw new IllegalArgumentException("Invalid catalog service: " + command.catalogService());
                }
            }
            
            ServiceTemplate template = new ServiceTemplate(
                code,
                catalogService,              // Optional - link to catalog
                command.customName(),         // Required - workshop's custom name
                command.description(),
                command.estimatedDurationMinutes(),
                basePrice
            );
            
            workshop.addServiceTemplate(template);
            workshopRepository.save(workshop);
            
            LOGGER.info("Service template added successfully to workshop: {}", command.workshopId());
            return template;
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid argument: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding service template: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add service template", e);
        }
    }
    
    @Override
    public ServiceTemplate handle(UpdateServiceTemplateCommand command) {
        LOGGER.info("Updating service template ID: {} in workshop ID: {}", 
                    command.serviceTemplateId(), command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            ServiceTemplate template = workshop.findServiceTemplateById(command.serviceTemplateId())
                .orElseThrow(() -> new RuntimeException("Service template not found"));
            
            template.updateDetails(
                command.title(),
                command.description(),
                command.baseDurationMinutes()
            );
            
            if (command.basePriceAmount() != null && command.currency() != null) {
                Money newPrice = new Money(command.basePriceAmount(), command.currency());
                template.updateBasePrice(newPrice);
            }
            
            workshopRepository.save(workshop);
            LOGGER.info("Service template updated successfully");
            
            return template;
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating service template: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update service template", e);
        }
    }
    
    @Override
    public void handle(com.atg.autonexo.backend.workshop.domain.model.commands.AddCapabilityTagCommand command) {
        LOGGER.info("Adding capability tag to workshop ID: {}", command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            workshop.addCapabilityTag(command.tag());
            workshopRepository.save(workshop);
            
            LOGGER.info("Capability tag added successfully to workshop: {}", command.workshopId());
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding capability tag: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add capability tag", e);
        }
    }
    
    @Override
    public void handle(com.atg.autonexo.backend.workshop.domain.model.commands.UpdateWorkshopCapabilityTagsCommand command) {
        LOGGER.info("Updating capability tags for workshop ID: {}", command.workshopId());
        
        try {
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            workshop.setCapabilityTags(command.tags());
            workshopRepository.save(workshop);
            
            LOGGER.info("Capability tags updated successfully for workshop: {}", command.workshopId());
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating capability tags: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update capability tags", e);
        }
    }
}

