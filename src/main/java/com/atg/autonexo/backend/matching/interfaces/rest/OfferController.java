package com.atg.autonexo.backend.matching.interfaces.rest;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.atg.autonexo.backend.matching.application.internal.commandservices.OfferCommandServiceImpl;
import com.atg.autonexo.backend.matching.application.internal.queryservices.OfferQueryServiceImpl;
import com.atg.autonexo.backend.workshop.domain.services.WorkshopQueryService;
import com.atg.autonexo.backend.matching.domain.exceptions.OfferNotFoundException;
import com.atg.autonexo.backend.matching.domain.model.queries.GetOffersByServiceRequestQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetUserOffersQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopOffersQuery;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.CreateOfferResource;
import com.atg.autonexo.backend.matching.interfaces.rest.transform.OfferCommandFromResourceAssembler;
import com.atg.autonexo.backend.matching.interfaces.rest.transform.OfferResourceFromEntityAssembler;
import com.atg.autonexo.backend.matching.interfaces.rest.transform.ServiceBookingResourceFromEntityAssembler;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;

import jakarta.validation.Valid;

/**
 * REST Controller for offer operations.
 */
@RestController
@RequestMapping("/api/offers")
public class OfferController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);
    
    private final OfferCommandServiceImpl commandService;
    private final OfferQueryServiceImpl queryService;
    private final WorkshopQueryService workshopQueryService;

    public OfferController(
            OfferCommandServiceImpl commandService,
            OfferQueryServiceImpl queryService,
            WorkshopQueryService workshopQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.workshopQueryService = workshopQueryService;
    }
    
    @PostMapping
    public ResponseEntity<?> createOffer(@Valid @RequestBody CreateOfferResource resource) {
        try {
            Long workshopId = WorkshopContext.getCurrentWorkshopIdAsLong();
            var command = OfferCommandFromResourceAssembler.toCommandFromResource(resource, workshopId);
            var offer = commandService.handle(command);
            var resourceResponse = OfferResourceFromEntityAssembler.toResourceFromEntity(offer, workshopQueryService);
            return ResponseEntity.status(HttpStatus.CREATED).body(resourceResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error creating offer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while creating the offer");
        }
    }
    
    @GetMapping("/service-requests/{requestId}")
    public ResponseEntity<?> getOffersByServiceRequest(@PathVariable Long requestId) {
        try {
            var query = new GetOffersByServiceRequestQuery(requestId);
            var offers = queryService.handle(query);
            var resources = offers.stream()
                .map(o -> OfferResourceFromEntityAssembler.toResourceFromEntity(o, workshopQueryService))
                .collect(Collectors.toList());
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            LOGGER.error("Error getting offers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while getting offers");
        }
    }
    
    @GetMapping("/my-workshop")
    public ResponseEntity<?> getMyWorkshopOffers(@RequestParam(required = false) String status) {
        try {
            Long workshopId = WorkshopContext.getCurrentWorkshopIdAsLong();
            OfferStatus statusEnum = status != null ? OfferStatus.valueOf(status) : null;
            var query = new GetWorkshopOffersQuery(new WorkshopId(workshopId), statusEnum);
            var offers = queryService.handle(query);
            var resources = offers.stream()
                .map(o -> OfferResourceFromEntityAssembler.toResourceFromEntity(o, workshopQueryService))
                .collect(Collectors.toList());
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error getting workshop offers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while getting workshop offers");
        }
    }
    
    @GetMapping("/my-requests")
    public ResponseEntity<?> getMyOffers(@RequestParam(required = false) String status) {
        try {
            Long userId = getCurrentUserId();
            OfferStatus statusEnum = status != null ? OfferStatus.valueOf(status) : null;
            var query = new GetUserOffersQuery(userId, statusEnum);
            var offers = queryService.handle(query);
            var resources = offers.stream()
                .map(o -> OfferResourceFromEntityAssembler.toResourceFromEntity(o, workshopQueryService))
                .collect(Collectors.toList());
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error getting user offers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while getting user offers");
        }
    }
    
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptOffer(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            var command = OfferCommandFromResourceAssembler.toAcceptCommand(id, userId);
            var serviceBooking = commandService.handle(command);
            var resource = ServiceBookingResourceFromEntityAssembler.toResourceFromEntity(serviceBooking);
            return ResponseEntity.ok(resource);
        } catch (OfferNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error accepting offer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while accepting offer");
        }
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectOffer(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            var command = OfferCommandFromResourceAssembler.toRejectCommand(id, userId);
            commandService.handle(command);
            return ResponseEntity.noContent().build();
        } catch (OfferNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error rejecting offer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while rejecting offer");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> withdrawOffer(@PathVariable Long id) {
        try {
            Long workshopId = WorkshopContext.getCurrentWorkshopIdAsLong();
            var command = OfferCommandFromResourceAssembler.toWithdrawCommand(id, workshopId);
            commandService.handle(command);
            return ResponseEntity.noContent().build();
        } catch (OfferNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error withdrawing offer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while withdrawing offer");
        }
    }
    
    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new SecurityException("User is not authenticated");
        }
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        throw new SecurityException("Unable to extract user ID from authentication");
    }
}

