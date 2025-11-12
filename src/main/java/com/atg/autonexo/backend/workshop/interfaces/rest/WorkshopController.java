package com.atg.autonexo.backend.workshop.interfaces.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atg.autonexo.backend.shared.infrastructure.media.cloudinary.CloudinaryService;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;
import com.atg.autonexo.backend.workshop.application.internal.commandservices.WorkshopCommandServiceImpl;
import com.atg.autonexo.backend.workshop.application.internal.queryservices.WorkshopQueryServiceImpl;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopAlreadyExistsException;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopContextNotFoundException;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.commands.AddCapabilityTagCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.AddLocationCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.AddServiceTemplateCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.AddStaffMemberCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.CreateWorkshopCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.UpdateWorkshopCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.UpdateWorkshopCapabilityTagsCommand;
import com.atg.autonexo.backend.workshop.domain.model.entities.Location;
import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetAllWorkshopsQuery;
import com.atg.autonexo.backend.workshop.domain.model.commands.UpdateSubscriptionCommand;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetWorkshopByIdQuery;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetWorkshopByOwnerQuery;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetWorkshopsByCapabilityTagQuery;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.AddLocationResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.AddServiceTemplateResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.AddStaffMemberResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.CreateWorkshopResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.LocationResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.ServiceTemplateResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.SubscriptionResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.UpdateWorkshopResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.UpdateSubscriptionResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.WorkshopResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.AddLocationCommandFromResourceAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.AddServiceTemplateCommandFromResourceAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.CreateWorkshopCommandFromResourceAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.LocationResourceFromEntityAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.ServiceTemplateResourceFromEntityAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.UpdateWorkshopCommandFromResourceAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.WorkshopResourceFromEntityAssembler;

import jakarta.validation.Valid;

/**
 * Workshop REST Controller
 * <p>
 * This controller handles HTTP requests for workshop-related operations including
 * workshop creation, location management, staff management, and service templates.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/workshops")
public class WorkshopController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkshopController.class);
    
    private final WorkshopCommandServiceImpl workshopCommandService;
    private final WorkshopQueryServiceImpl workshopQueryService;
    private final CloudinaryService cloudinaryService;
    
    public WorkshopController(
            WorkshopCommandServiceImpl workshopCommandService,
            WorkshopQueryServiceImpl workshopQueryService,
            CloudinaryService cloudinaryService) {
        this.workshopCommandService = workshopCommandService;
        this.workshopQueryService = workshopQueryService;
        this.cloudinaryService = cloudinaryService;
    }
    
    /**
     * Create a new workshop
     * @param resource the workshop creation data
     * @return ResponseEntity with created workshop
     */
    @PostMapping
    public ResponseEntity<?> createWorkshop(@Valid @RequestBody CreateWorkshopResource resource) {
        try {
            LOGGER.info("Processing create workshop request for owner user ID: {}", resource.ownerUserId());
            
            CreateWorkshopCommand command = CreateWorkshopCommandFromResourceAssembler.toCommandFromResource(resource);
            Workshop workshop = workshopCommandService.handle(command);
            WorkshopResource workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop);
            
            LOGGER.info("Workshop created successfully with ID: {}", workshop.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(workshopResource);
            
        } catch (WorkshopAlreadyExistsException e) {
            LOGGER.warn("Workshop creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Workshop creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during workshop creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during workshop creation");
        }
    }
    
    /**
     * Get my workshop (from authenticated user context)
     * @return ResponseEntity with workshop information
     */
    @GetMapping("/my-workshop")
    public ResponseEntity<?> getMyWorkshop() {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.debug("Processing get my workshop request for ID: {}", workshopId);
            
            Optional<Workshop> workshopOptional = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId));
            
            if (workshopOptional.isEmpty()) {
                throw new WorkshopNotFoundException(workshopId);
            }
            
            Workshop workshop = workshopOptional.get();
            WorkshopResource workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop);
            
            return ResponseEntity.ok(workshopResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Workshop not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving my workshop: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving workshop");
        }
    }
    
    /**
     * Get workshop by ID (public endpoint)
     * @param workshopId the workshop ID
     * @return ResponseEntity with workshop information
     */
    @GetMapping("/{workshopId}")
    public ResponseEntity<?> getWorkshopById(@PathVariable Long workshopId) {
        try {
            LOGGER.debug("Processing get workshop by ID request: {}", workshopId);
            
            Optional<Workshop> workshopOptional = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId));
            
            if (workshopOptional.isEmpty()) {
                throw new WorkshopNotFoundException(workshopId);
            }
            
            Workshop workshop = workshopOptional.get();
            WorkshopResource workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop);
            
            return ResponseEntity.ok(workshopResource);
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Workshop not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving workshop: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving workshop");
        }
    }
    
    /**
     * Get workshop by owner user ID
     * @param ownerUserId the owner user ID
     * @return ResponseEntity with workshop information
     */
    @GetMapping("/by-owner/{ownerUserId}")
    public ResponseEntity<?> getWorkshopByOwner(@PathVariable Long ownerUserId) {
        try {
            LOGGER.debug("Processing get workshop by owner request: {}", ownerUserId);
            
            Optional<Workshop> workshopOptional = workshopQueryService.handle(new GetWorkshopByOwnerQuery(ownerUserId));
            
            if (workshopOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No workshop found for owner user ID: " + ownerUserId);
            }
            
            Workshop workshop = workshopOptional.get();
            WorkshopResource workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop);
            
            return ResponseEntity.ok(workshopResource);
            
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving workshop by owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving workshop");
        }
    }
    
    /**
     * Get all active workshops
     * @return ResponseEntity with list of workshops
     */
    @GetMapping
    public ResponseEntity<?> getAllWorkshops() {
        try {
            LOGGER.debug("Processing get all workshops request");
            
            List<Workshop> workshops = workshopQueryService.handle(new GetAllWorkshopsQuery());
            List<WorkshopResource> workshopResources = workshops.stream()
                    .map(WorkshopResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            
            return ResponseEntity.ok(workshopResources);
            
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving all workshops: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving workshops");
        }
    }
    
    /**
     * Get workshops by capability tag
     * @param tag the capability tag to search for (enum name)
     * @return ResponseEntity with list of workshops
     */
    @GetMapping("/by-tag")
    public ResponseEntity<?> getWorkshopsByCapabilityTag(@RequestParam String tag) {
        try {
            LOGGER.debug("Processing get workshops by capability tag request: {}", tag);
            
            // Parse the tag enum
            com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag capabilityTag;
            try {
                capabilityTag = com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag.fromString(tag);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid capability tag: " + tag);
            }
            
            List<Workshop> workshops = workshopQueryService.handle(new GetWorkshopsByCapabilityTagQuery(capabilityTag));
            List<WorkshopResource> workshopResources = workshops.stream()
                    .map(WorkshopResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            
            return ResponseEntity.ok(workshopResources);
            
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving workshops by capability tag: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving workshops");
        }
    }
    
    /**
     * Update my workshop basic information
     * @param resource the update data
     * @return ResponseEntity with updated workshop
     */
    @PutMapping
    public ResponseEntity<?> updateMyWorkshop(@Valid @RequestBody UpdateWorkshopResource resource) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing update workshop request for ID: {}", workshopId);
            
            UpdateWorkshopCommand command = UpdateWorkshopCommandFromResourceAssembler
                    .toCommandFromResource(workshopId, resource);
            Workshop workshop = workshopCommandService.handle(command);
            WorkshopResource workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop);
            
            LOGGER.info("Workshop updated successfully: {}", workshopId);
            return ResponseEntity.ok(workshopResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Workshop update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during workshop update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during workshop update");
        }
    }
    
    /**
     * Add a location to my workshop
     * @param resource the location data
     * @return ResponseEntity with created location
     */
    @PostMapping("/locations")
    public ResponseEntity<?> addLocation(@Valid @RequestBody AddLocationResource resource) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing add location request for workshop ID: {}", workshopId);
            
            AddLocationCommand command = AddLocationCommandFromResourceAssembler
                    .toCommandFromResource(workshopId, resource);
            Location location = workshopCommandService.handle(command);
            LocationResource locationResource = LocationResourceFromEntityAssembler.toResourceFromEntity(location);
            
            LOGGER.info("Location added successfully to workshop: {}", workshopId);
            return ResponseEntity.status(HttpStatus.CREATED).body(locationResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Add location failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error adding location: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while adding location");
        }
    }
    
    /**
     * Add a staff member to my workshop
     * @deprecated Use invitation flow instead via /api/v1/invitations
     * @param resource the staff member data
     * @return ResponseEntity with success message
     */
    @Deprecated
    @PostMapping("/staff")
    public ResponseEntity<?> addStaffMember(@Valid @RequestBody AddStaffMemberResource resource) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.warn("Using deprecated addStaffMember endpoint. Use invitation flow instead.");
            LOGGER.info("Processing add staff member request for workshop ID: {}", workshopId);
            
            AddStaffMemberCommand command = new AddStaffMemberCommand(
                workshopId,
                resource.userId(),
                resource.primaryLocationId()
            );
            StaffMember staffMember = workshopCommandService.handle(command);
            
            LOGGER.info("Staff member added successfully to workshop: {}", workshopId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Staff member added successfully with ID: " + staffMember.getId() + 
                          ". Note: This endpoint is deprecated. Please use the invitation flow at /api/v1/invitations");
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Add staff member failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error adding staff member: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while adding staff member");
        }
    }
    
    /**
     * Add a service template to my workshop
     * @param resource the service template data
     * @return ResponseEntity with created service template
     */
    @PostMapping("/service-templates")
    public ResponseEntity<?> addServiceTemplate(@Valid @RequestBody AddServiceTemplateResource resource) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing add service template request for workshop ID: {}", workshopId);
            
            AddServiceTemplateCommand command = AddServiceTemplateCommandFromResourceAssembler
                    .toCommandFromResource(workshopId, resource);
            ServiceTemplate template = workshopCommandService.handle(command);
            ServiceTemplateResource templateResource = ServiceTemplateResourceFromEntityAssembler
                    .toResourceFromEntity(template);
            
            LOGGER.info("Service template added successfully to workshop: {}", workshopId);
            return ResponseEntity.status(HttpStatus.CREATED).body(templateResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Add service template failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error adding service template: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while adding service template");
        }
    }
    
    /**
     * Add a capability tag to my workshop
     * @param tag the capability tag to add (enum name: LIGHT_VEHICLES, TOYOTA, DIESEL_SPECIALIST, etc.)
     * @return ResponseEntity with success message
     */
    @PostMapping("/tags")
    public ResponseEntity<?> addCapabilityTag(@RequestParam String tag) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing add capability tag request for workshop ID: {}", workshopId);
            
            // Parse the tag enum
            com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag capabilityTag;
            try {
                capabilityTag = com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag.fromString(tag);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid capability tag: " + tag);
            }
            
            AddCapabilityTagCommand command = new AddCapabilityTagCommand(workshopId, capabilityTag);
            workshopCommandService.handle(command);
            
            LOGGER.info("Capability tag added successfully to workshop: {}", workshopId);
            return ResponseEntity.ok("Capability tag added successfully");
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Add capability tag failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error adding capability tag: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while adding capability tag");
        }
    }
    
    /**
     * Update all capability tags for my workshop (replaces existing tags)
     * @param tags Set of tag names (enum names)
     * @return ResponseEntity with success message
     */
    @PutMapping("/tags")
    public ResponseEntity<?> updateCapabilityTags(@RequestBody Set<String> tags) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing update capability tags request for workshop ID: {}", workshopId);
            
            // Parse all tags
            Set<com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag> capabilityTags = new HashSet<>();
            if (tags != null) {
                for (String tag : tags) {
                    try {
                        capabilityTags.add(com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag.fromString(tag));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Invalid capability tag: " + tag);
                    }
                }
            }
            
            UpdateWorkshopCapabilityTagsCommand command = new UpdateWorkshopCapabilityTagsCommand(workshopId, capabilityTags);
            workshopCommandService.handle(command);
            
            LOGGER.info("Capability tags updated successfully for workshop: {}", workshopId);
            return ResponseEntity.ok("Capability tags updated successfully");
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Update capability tags failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error updating capability tags: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating capability tags");
        }
    }
    
    // === Media Management Endpoints ===
    
    /**
     * Upload logo for my workshop
     * @param file the logo image file
     * @return ResponseEntity with uploaded image URL
     */
    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing upload logo request for workshop ID: {}", workshopId);
            
            // Validate image
            if (!isValidImage(file)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid image file. Supported formats: JPG, PNG, GIF, WEBP. Max size: 5MB");
            }
            
            // Get workshop
            Workshop workshop = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId))
                    .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            // Delete old logo if exists
            if (workshop.getLogoUrl() != null) {
                try {
                    String publicId = extractPublicIdFromUrl(workshop.getLogoUrl());
                    cloudinaryService.deleteFile(publicId);
                } catch (Exception e) {
                    LOGGER.warn("Failed to delete old logo: {}", e.getMessage());
                }
            }
            
            // Upload new logo
            var uploadResult = cloudinaryService.uploadFile(file, "workshops/" + workshopId + "/logo");
            String logoUrl = (String) uploadResult.get("secure_url");
            workshop.setLogoUrl(logoUrl);
            workshopCommandService.handle(new UpdateWorkshopCommand(
                workshopId, 
                workshop.getName(), 
                workshop.getShortDescription(), 
                workshop.getLegalName()
            ));
            
            LOGGER.info("Logo uploaded successfully for workshop: {}", workshopId);
            return ResponseEntity.ok(new ImageUploadResponse(logoUrl));
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Upload logo failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error uploading logo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while uploading logo");
        }
    }
    
    /**
     * Add photo to my workshop carousel
     * @param file the photo image file
     * @return ResponseEntity with uploaded image URL
     */
    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPhoto(@RequestParam("file") MultipartFile file) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing add photo request for workshop ID: {}", workshopId);
            
            // Validate image
            if (!isValidImage(file)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid image file. Supported formats: JPG, PNG, GIF, WEBP. Max size: 5MB");
            }
            
            // Get workshop
            Workshop workshop = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId))
                    .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            // Check photo limit
            if (workshop.getPhotoUrls().size() >= 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Maximum of 10 photos allowed per workshop");
            }
            
            // Upload photo
            var uploadResult = cloudinaryService.uploadFile(file, "workshops/" + workshopId + "/photos");
            String photoUrl = (String) uploadResult.get("secure_url");
            workshop.addPhoto(photoUrl);
            workshopCommandService.handle(new UpdateWorkshopCommand(
                workshopId, 
                workshop.getName(), 
                workshop.getShortDescription(), 
                workshop.getLegalName()
            ));
            
            LOGGER.info("Photo added successfully to workshop: {}", workshopId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ImageUploadResponse(photoUrl));
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Add photo failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error adding photo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while adding photo");
        }
    }
    
    /**
     * Delete photo from my workshop carousel
     * @param photoIndex the index of the photo to delete (0-based)
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/photos/{photoIndex}")
    public ResponseEntity<?> deletePhoto(@PathVariable Integer photoIndex) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing delete photo request for workshop ID: {}", workshopId);
            
            // Get workshop
            Workshop workshop = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId))
                    .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            // Validate index
            if (photoIndex < 0 || photoIndex >= workshop.getPhotoUrls().size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid photo index");
            }
            
            // Get photo URL and delete from storage
            String photoUrl = workshop.getPhotoUrls().get(photoIndex);
            try {
                String publicId = extractPublicIdFromUrl(photoUrl);
                cloudinaryService.deleteFile(publicId);
            } catch (Exception e) {
                LOGGER.warn("Failed to delete photo from storage: {}", e.getMessage());
            }
            
            // Remove from workshop
            workshop.removePhotoByIndex(photoIndex);
            workshopCommandService.handle(new UpdateWorkshopCommand(
                workshopId, 
                workshop.getName(), 
                workshop.getShortDescription(), 
                workshop.getLegalName()
            ));
            
            LOGGER.info("Photo deleted successfully from workshop: {}", workshopId);
            return ResponseEntity.ok("Photo deleted successfully");
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Delete photo failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error deleting photo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while deleting photo");
        }
    }
    
    /**
     * Simple response for image uploads
     */
    private record ImageUploadResponse(String url) {}
    
    // === Helper Methods ===
    
    /**
     * Gets the workshop ID from the current request context
     * @return the workshop ID
     * @throws WorkshopContextNotFoundException if no workshop context is found
     */
    private Long getWorkshopIdFromContext() {
        if (!WorkshopContext.hasWorkshopContext()) {
            throw new WorkshopContextNotFoundException();
        }
        return WorkshopContext.getCurrentWorkshopIdAsLong();
    }
    
    /**
     * Validates if a file is a valid image
     */
    private boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            LOGGER.warn("File is null or empty");
            return false;
        }
        
        // Check file size (5MB max)
        long maxFileSize = 5 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            LOGGER.warn("File size exceeds maximum: {} bytes", file.getSize());
            return false;
        }
        
        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            LOGGER.warn("Invalid filename: {}", filename);
            return false;
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "webp");
        if (!allowedExtensions.contains(extension)) {
            LOGGER.warn("Invalid file extension: {}", extension);
            return false;
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            LOGGER.warn("Invalid content type: {}", contentType);
            return false;
        }
        
        return true;
    }
    
    /**
     * Extracts the public_id from a Cloudinary URL
     * Example: https://res.cloudinary.com/cloud-name/image/upload/v1234567890/folder/file.jpg
     * Returns: folder/file
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Pattern to extract public_id from Cloudinary URL
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/upload/(?:v\\d+/)?(.+?)(?:\\.[^.]+)?$");
            java.util.regex.Matcher matcher = pattern.matcher(imageUrl);
            
            if (matcher.find()) {
                return matcher.group(1);
            }
            
            throw new IllegalArgumentException("Invalid Cloudinary URL format: " + imageUrl);
            
        } catch (Exception e) {
            LOGGER.error("Failed to extract public_id from URL: {}", imageUrl, e);
            throw new RuntimeException("Failed to extract public_id from URL", e);
        }
    }
    
    // === Catalog Endpoints ===
    
    /**
     * Get all available service categories
     * @return ResponseEntity with list of categories
     */
    @GetMapping("/catalog/categories")
    public ResponseEntity<?> getServiceCategories() {
        try {
            var categories = com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCategory.values();
            List<Map<String, String>> categoryList = new ArrayList<>();
            
            for (var category : categories) {
                categoryList.add(Map.of(
                    "code", category.name(),
                    "displayName", category.getDisplayName()
                ));
            }
            
            return ResponseEntity.ok(categoryList);
            
        } catch (Exception e) {
            LOGGER.error("Error retrieving service categories: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving service categories");
        }
    }
    
    /**
     * Get all available services in the catalog
     * @return ResponseEntity with list of services
     */
    @GetMapping("/catalog/services")
    public ResponseEntity<?> getServiceCatalog() {
        try {
            var services = com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog.values();
            List<Map<String, Object>> serviceList = new ArrayList<>();
            
            for (var service : services) {
                serviceList.add(Map.of(
                    "code", service.name(),
                    "displayName", service.getDisplayName(),
                    "category", service.getCategory().name(),
                    "categoryDisplayName", service.getCategory().getDisplayName()
                ));
            }
            
            return ResponseEntity.ok(serviceList);
            
        } catch (Exception e) {
            LOGGER.error("Error retrieving service catalog: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving service catalog");
        }
    }
    
    /**
     * Get all available capability tags
     * @return ResponseEntity with list of capability tags
     */
    @GetMapping("/catalog/capability-tags")
    public ResponseEntity<?> getCapabilityTags() {
        try {
            var tags = com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag.values();
            List<Map<String, String>> tagList = new ArrayList<>();
            
            for (var tag : tags) {
                tagList.add(Map.of(
                    "code", tag.name(),
                    "displayName", tag.getDisplayName(),
                    "category", tag.getCategory().name()
                ));
            }
            
            return ResponseEntity.ok(tagList);
            
        } catch (Exception e) {
            LOGGER.error("Error retrieving capability tags: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving capability tags");
        }
    }
    
    /**
     * Get subscription status for my workshop
     * @return ResponseEntity with subscription information
     */
    @GetMapping("/my-workshop/subscription")
    public ResponseEntity<?> getMyWorkshopSubscription() {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.debug("Processing get subscription request for workshop ID: {}", workshopId);
            
            Optional<Workshop> workshopOptional = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId));
            
            if (workshopOptional.isEmpty()) {
                throw new WorkshopNotFoundException(workshopId);
            }
            
            Workshop workshop = workshopOptional.get();
            SubscriptionResource subscriptionResource = new SubscriptionResource(
                workshop.getSubscriptionStatus(),
                workshop.getSubscriptionTier(),
                workshop.getSubscriptionExpiresAt(),
                workshop.isSubscriptionActive(),
                workshop.canAccessPremiumFeatures()
            );
            
            return ResponseEntity.ok(subscriptionResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Workshop not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving subscription");
        }
    }
    
    /**
     * Update subscription for my workshop (called by Payment BC)
     * @param resource the subscription update data
     * @return ResponseEntity with updated subscription information
     */
    @PutMapping("/my-workshop/subscription")
    public ResponseEntity<?> updateMyWorkshopSubscription(@Valid @RequestBody UpdateSubscriptionResource resource) {
        try {
            Long workshopId = getWorkshopIdFromContext();
            LOGGER.info("Processing update subscription request for workshop ID: {}", workshopId);
            
            UpdateSubscriptionCommand command = new UpdateSubscriptionCommand(
                workshopId,
                resource.status(),
                resource.tier(),
                resource.expiresAt()
            );
            
            Workshop workshop = workshopCommandService.handle(command);
            
            SubscriptionResource subscriptionResource = new SubscriptionResource(
                workshop.getSubscriptionStatus(),
                workshop.getSubscriptionTier(),
                workshop.getSubscriptionExpiresAt(),
                workshop.isSubscriptionActive(),
                workshop.canAccessPremiumFeatures()
            );
            
            LOGGER.info("Subscription updated successfully for workshop: {}", workshopId);
            return ResponseEntity.ok(subscriptionResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Update subscription failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during subscription update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during subscription update");
        }
    }
}

