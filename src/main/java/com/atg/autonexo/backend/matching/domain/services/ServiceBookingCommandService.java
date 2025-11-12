package com.atg.autonexo.backend.matching.domain.services;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceBooking;
import com.atg.autonexo.backend.matching.domain.model.commands.CancelServiceBookingCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ConfirmPickupCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ConfirmScheduleCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.MarkCompletedCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ProposeScheduleChangeCommand;

/**
 * Domain service interface for ServiceBooking command operations.
 */
public interface ServiceBookingCommandService {
    
    ServiceBooking handle(ConfirmScheduleCommand command);
    
    ServiceBooking handle(ProposeScheduleChangeCommand command);
    
    ServiceBooking handle(MarkCompletedCommand command);
    
    ServiceBooking handle(ConfirmPickupCommand command);
    
    void handle(CancelServiceBookingCommand command);
}

