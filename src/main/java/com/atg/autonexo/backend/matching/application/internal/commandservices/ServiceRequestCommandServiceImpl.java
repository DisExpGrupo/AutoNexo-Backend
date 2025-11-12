package com.atg.autonexo.backend.matching.application.internal.commandservices;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.exceptions.ServiceRequestNotFoundException;
import com.atg.autonexo.backend.matching.domain.exceptions.InvalidServiceRequestStatusException;
import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.domain.model.commands.CancelServiceRequestCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.CreateServiceRequestCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.RejectServiceRequestCommand;
import com.atg.autonexo.backend.matching.domain.services.MatchingService;
import com.atg.autonexo.backend.matching.domain.services.ServiceRequestCommandService;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceRequestRepository;

/**
 * Implementation of ServiceRequestCommandService.
 */
@Service
@Transactional
public class ServiceRequestCommandServiceImpl implements ServiceRequestCommandService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRequestCommandServiceImpl.class);
    
    private final ServiceRequestRepository serviceRequestRepository;
    private final MatchingService matchingService;
    
    public ServiceRequestCommandServiceImpl(
            ServiceRequestRepository serviceRequestRepository,
            MatchingService matchingService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.matchingService = matchingService;
    }
    
    @Override
    public ServiceRequest handle(CreateServiceRequestCommand command) {
        LOGGER.info("Creating service request for vehicle {} by user {}", command.vehicleId(), command.userId().id());
        
        ServiceRequest serviceRequest = new ServiceRequest(
            command.userId(),
            command.vehicleId(),
            command.requestedServices(),
            command.description(),
            command.userLocation(),
            command.searchRadius()
        );
        
        // Save the request first to get an ID
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        LOGGER.info("Service request created with ID: {}", saved.getId());
        
        // Perform automatic matching
        try {
            LOGGER.info("Starting automatic matching for service request {}", saved.getId());
            List<MatchingService.WorkshopMatchResult> matches = matchingService.findMatchingWorkshops(
                command.userLocation(),
                command.searchRadius().valueInKm(),
                command.requestedServices(),
                Optional.empty() // No minimum rating filter
            );
            
            LOGGER.info("Found {} matching workshops for request {}", matches.size(), saved.getId());
            
            // Add each match to the service request
            for (MatchingService.WorkshopMatchResult match : matches) {
                saved.addMatch(
                    match.workshopId(),
                    match.matchScore(),
                    match.distanceKm(),
                    match.matchingServices()
                );
            }
            
            // Save the request with matches
            saved = serviceRequestRepository.save(saved);
            LOGGER.info("Service request {} matched with {} workshops", saved.getId(), matches.size());
            
        } catch (Exception e) {
            LOGGER.error("Error during automatic matching for request {}: {}", saved.getId(), e.getMessage(), e);
            // Continue - request is still valid even if matching fails
        }
        
        return saved;
    }
    
    @Override
    public void handle(CancelServiceRequestCommand command) {
        LOGGER.info("Cancelling service request {} by user {}", command.serviceRequestId(), command.userId());
        
        ServiceRequest serviceRequest = serviceRequestRepository.findById(command.serviceRequestId())
            .orElseThrow(() -> new ServiceRequestNotFoundException(command.serviceRequestId()));
        
        // Verify ownership
        if (!serviceRequest.getUserId().id().equals(command.userId())) {
            throw new SecurityException("User does not own this service request");
        }
        
        // Verify status
        if (serviceRequest.getStatus() != com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceRequestStatus.PENDING) {
            throw new InvalidServiceRequestStatusException(
                command.serviceRequestId(),
                serviceRequest.getStatus(),
                "cancel"
            );
        }
        
        serviceRequest.cancel();
        serviceRequestRepository.save(serviceRequest);
        LOGGER.info("Service request {} cancelled successfully", command.serviceRequestId());
    }
    
    @Override
    public void handle(RejectServiceRequestCommand command) {
        LOGGER.info("Rejecting service request {} by workshop {}", command.serviceRequestId(), command.workshopId().id());
        
        ServiceRequest serviceRequest = serviceRequestRepository.findById(command.serviceRequestId())
            .orElseThrow(() -> new ServiceRequestNotFoundException(command.serviceRequestId()));
        
        serviceRequest.rejectByWorkshop(command.workshopId());
        serviceRequestRepository.save(serviceRequest);
        LOGGER.info("Service request {} rejected by workshop {}", command.serviceRequestId(), command.workshopId().id());
    }
}

