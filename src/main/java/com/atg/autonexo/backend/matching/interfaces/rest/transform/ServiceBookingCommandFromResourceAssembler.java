package com.atg.autonexo.backend.matching.interfaces.rest.transform;

import java.util.List;
import java.util.stream.Collectors;

import com.atg.autonexo.backend.matching.domain.model.commands.CancelServiceBookingCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ConfirmPickupCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ConfirmScheduleCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.MarkCompletedCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ProposeScheduleChangeCommand;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.CancelServiceBookingResource;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.ConfirmScheduleResource;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.MarkCompletedResource;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.ProposeScheduleChangeResource;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Assembler for converting REST resources to ServiceBooking commands.
 */
public class ServiceBookingCommandFromResourceAssembler {
    
    public static ConfirmScheduleCommand toConfirmScheduleCommand(Long serviceBookingId, ConfirmScheduleResource resource, Long userId) {
        return new ConfirmScheduleCommand(serviceBookingId, resource.scheduledDate(), userId);
    }
    
    public static ProposeScheduleChangeCommand toProposeScheduleChangeCommand(Long serviceBookingId, ProposeScheduleChangeResource resource, Long userId) {
        return new ProposeScheduleChangeCommand(serviceBookingId, resource.newScheduledDate(), userId);
    }
    
    public static MarkCompletedCommand toMarkCompletedCommand(Long serviceBookingId, MarkCompletedResource resource, Long workshopId) {
        List<MarkCompletedCommand.ServicePerformedData> services = resource.services().stream()
            .map(s -> new MarkCompletedCommand.ServicePerformedData(
                ServiceCatalog.fromString(s.serviceType()),
                s.description(),
                s.cost()
            ))
            .collect(Collectors.toList());
        
        return new MarkCompletedCommand(
            serviceBookingId,
            new WorkshopId(workshopId),
            resource.mileage(),
            services,
            resource.observations(),
            resource.imageUrls(),
            resource.finalPriceAmount(),
            resource.currency()
        );
    }
    
    public static ConfirmPickupCommand toConfirmPickupCommand(Long serviceBookingId, Long userId) {
        return new ConfirmPickupCommand(serviceBookingId, userId);
    }
    
    public static CancelServiceBookingCommand toCancelCommand(Long serviceBookingId, Long userId, CancelServiceBookingResource resource) {
        return new CancelServiceBookingCommand(serviceBookingId, new UserId(userId), resource.cancellationReason());
    }
}

