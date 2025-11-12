package com.atg.autonexo.backend.matching.domain.services;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.domain.model.commands.CancelServiceRequestCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.CreateServiceRequestCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.RejectServiceRequestCommand;

/**
 * Domain service interface for ServiceRequest command operations.
 */
public interface ServiceRequestCommandService {
    
    ServiceRequest handle(CreateServiceRequestCommand command);
    
    void handle(CancelServiceRequestCommand command);
    
    void handle(RejectServiceRequestCommand command);
}

