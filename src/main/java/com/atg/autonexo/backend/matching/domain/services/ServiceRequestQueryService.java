package com.atg.autonexo.backend.matching.domain.services;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.domain.model.queries.GetServiceRequestByIdQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetUserServiceRequestsQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopAvailableRequestsQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopReceivedRequestsQuery;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for ServiceRequest query operations.
 */
public interface ServiceRequestQueryService {
    
    Optional<ServiceRequest> handle(GetServiceRequestByIdQuery query);
    
    List<ServiceRequest> handle(GetUserServiceRequestsQuery query);
    
    List<ServiceRequest> handle(GetWorkshopAvailableRequestsQuery query);
    
    List<ServiceRequest> handle(GetWorkshopReceivedRequestsQuery query);
}

