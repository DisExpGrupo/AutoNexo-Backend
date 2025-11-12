package com.atg.autonexo.backend.matching.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.domain.model.queries.GetServiceRequestByIdQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetUserServiceRequestsQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopAvailableRequestsQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopReceivedRequestsQuery;
import com.atg.autonexo.backend.matching.domain.services.ServiceRequestQueryService;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceRequestRepository;

/**
 * Implementation of ServiceRequestQueryService.
 */
@Service
@Transactional(readOnly = true)
public class ServiceRequestQueryServiceImpl implements ServiceRequestQueryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRequestQueryServiceImpl.class);
    
    private final ServiceRequestRepository serviceRequestRepository;
    
    public ServiceRequestQueryServiceImpl(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }
    
    @Override
    public Optional<ServiceRequest> handle(GetServiceRequestByIdQuery query) {
        return serviceRequestRepository.findById(query.serviceRequestId());
    }
    
    @Override
    public List<ServiceRequest> handle(GetUserServiceRequestsQuery query) {
        if (query.status() != null) {
            return serviceRequestRepository.findByUserIdAndStatus(query.userId(), query.status());
        }
        return serviceRequestRepository.findByUserId(query.userId());
    }
    
    @Override
    public List<ServiceRequest> handle(GetWorkshopAvailableRequestsQuery query) {
        return serviceRequestRepository.findByStatusAndNotRejectedByWorkshop(
            com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceRequestStatus.PENDING,
            query.workshopId().id()
        );
    }
    
    @Override
    public List<ServiceRequest> handle(GetWorkshopReceivedRequestsQuery query) {
        return serviceRequestRepository.findByWorkshopOffers(query.workshopId().id());
    }
}

