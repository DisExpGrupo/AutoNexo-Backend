package com.atg.autonexo.backend.matching.application.internal.commandservices;

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
    
    public ServiceRequestCommandServiceImpl(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
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
        
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        LOGGER.info("Service request created with ID: {}", saved.getId());
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

