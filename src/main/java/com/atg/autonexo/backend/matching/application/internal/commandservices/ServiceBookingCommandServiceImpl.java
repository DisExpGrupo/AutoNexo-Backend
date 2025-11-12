package com.atg.autonexo.backend.matching.application.internal.commandservices;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.exceptions.ServiceBookingNotFoundException;
import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceBooking;
import com.atg.autonexo.backend.matching.domain.model.commands.CancelServiceBookingCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ConfirmPickupCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ConfirmScheduleCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.MarkCompletedCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.ProposeScheduleChangeCommand;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceBookingStatus;
import com.atg.autonexo.backend.matching.domain.services.ServiceBookingCommandService;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceBookingRepository;
import com.atg.autonexo.backend.matching.interfaces.acl.NotificationFacade;
import com.atg.autonexo.backend.matching.interfaces.acl.WorkshopFacade;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.vehicle.interfaces.acl.VehicleMaintenanceFacade;
import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

/**
 * Implementation of ServiceBookingCommandService.
 */
@Service
@Transactional
public class ServiceBookingCommandServiceImpl implements ServiceBookingCommandService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBookingCommandServiceImpl.class);
    
    private final ServiceBookingRepository serviceBookingRepository;
    private final VehicleMaintenanceFacade vehicleMaintenanceFacade;
    private final NotificationFacade notificationFacade;
    private final UserRepository userRepository;
    private final WorkshopRepository workshopRepository;
    private final WorkshopFacade workshopFacade;
    
    public ServiceBookingCommandServiceImpl(
            ServiceBookingRepository serviceBookingRepository,
            VehicleMaintenanceFacade vehicleMaintenanceFacade,
            NotificationFacade notificationFacade,
            UserRepository userRepository,
            WorkshopRepository workshopRepository,
            WorkshopFacade workshopFacade) {
        this.serviceBookingRepository = serviceBookingRepository;
        this.vehicleMaintenanceFacade = vehicleMaintenanceFacade;
        this.notificationFacade = notificationFacade;
        this.userRepository = userRepository;
        this.workshopRepository = workshopRepository;
        this.workshopFacade = workshopFacade;
    }
    
    @Override
    public ServiceBooking handle(ConfirmScheduleCommand command) {
        LOGGER.info("Confirming schedule for service booking {} by user {}", command.serviceBookingId(), command.userId());
        
        ServiceBooking serviceBooking = serviceBookingRepository.findById(command.serviceBookingId())
            .orElseThrow(() -> new ServiceBookingNotFoundException(command.serviceBookingId()));
        
        // Verify ownership (user or workshop can confirm)
        boolean isUser = serviceBooking.getUserId().id().equals(command.userId());
        boolean isWorkshop = serviceBooking.getWorkshopId().id().equals(command.userId());
        
        if (!isUser && !isWorkshop) {
            throw new SecurityException("User does not have access to this service booking");
        }
        
        serviceBooking.confirmSchedule(command.scheduledDate());
        ServiceBooking saved = serviceBookingRepository.save(serviceBooking);
        LOGGER.info("Schedule confirmed for service booking {}", command.serviceBookingId());
        return saved;
    }
    
    @Override
    public ServiceBooking handle(ProposeScheduleChangeCommand command) {
        LOGGER.info("Proposing schedule change for service booking {} by user {}", command.serviceBookingId(), command.userId());
        
        ServiceBooking serviceBooking = serviceBookingRepository.findById(command.serviceBookingId())
            .orElseThrow(() -> new ServiceBookingNotFoundException(command.serviceBookingId()));
        
        // Verify ownership (user or workshop can propose changes)
        boolean isUser = serviceBooking.getUserId().id().equals(command.userId());
        boolean isWorkshop = serviceBooking.getWorkshopId().id().equals(command.userId());
        
        if (!isUser && !isWorkshop) {
            throw new SecurityException("User does not have access to this service booking");
        }
        
        serviceBooking.proposeScheduleChange(command.newScheduledDate());
        ServiceBooking saved = serviceBookingRepository.save(serviceBooking);
        LOGGER.info("Schedule change proposed for service booking {}", command.serviceBookingId());
        return saved;
    }
    
    @Override
    public ServiceBooking handle(MarkCompletedCommand command) {
        LOGGER.info("Marking service booking {} as completed by workshop {}", command.serviceBookingId(), command.workshopId().id());
        
        ServiceBooking serviceBooking = serviceBookingRepository.findById(command.serviceBookingId())
            .orElseThrow(() -> new ServiceBookingNotFoundException(command.serviceBookingId()));
        
        // Verify workshop ownership
        if (!serviceBooking.getWorkshopId().id().equals(command.workshopId().id())) {
            throw new SecurityException("Workshop does not own this service booking");
        }
        
        // Mark as completed
        serviceBooking.markAsCompleted();
        serviceBooking.transitionToPendingPickup();
        
        // Update final price if different
        if (command.finalPriceAmount() != null && command.currency() != null) {
            Money finalPrice = new Money(command.finalPriceAmount(), command.currency());
            serviceBooking.updateFinalPrice(finalPrice);
        }
        
        ServiceBooking saved = serviceBookingRepository.save(serviceBooking);
        
        // Create maintenance record via ACL
        List<VehicleMaintenanceFacade.ServicePerformedData> servicesData = command.services().stream()
            .map(s -> new VehicleMaintenanceFacade.ServicePerformedData(
                s.serviceType(),
                s.description(),
                s.cost()
            ))
            .collect(Collectors.toList());
        
        vehicleMaintenanceFacade.createMaintenanceFromCompletedService(
            serviceBooking.getVehicleId(),
            command.workshopId(),
            LocalDate.now(), // Use current date as maintenance date
            command.mileage(),
            servicesData,
            command.observations(),
            command.imageUrls()
        );
        
        // Send notification to user
        try {
            User user = userRepository.findById(serviceBooking.getUserId().id()).orElse(null);
            if (user != null) {
                notificationFacade.notifyServiceCompleted(command.serviceBookingId(), user.getEmail());
            }
        } catch (Exception e) {
            LOGGER.error("Error sending service completed notification", e);
        }
        
        LOGGER.info("Service booking {} marked as completed and maintenance record created", command.serviceBookingId());
        return saved;
    }
    
    @Override
    public ServiceBooking handle(ConfirmPickupCommand command) {
        LOGGER.info("Confirming pickup for service booking {} by user {}", command.serviceBookingId(), command.userId());
        
        ServiceBooking serviceBooking = serviceBookingRepository.findById(command.serviceBookingId())
            .orElseThrow(() -> new ServiceBookingNotFoundException(command.serviceBookingId()));
        
        // Verify user ownership
        if (!serviceBooking.getUserId().id().equals(command.userId())) {
            throw new SecurityException("User does not own this service booking");
        }
        
        if (serviceBooking.getStatus() != ServiceBookingStatus.PENDING_PICKUP) {
            throw new IllegalStateException("Service booking must be in PENDING_PICKUP status to confirm pickup");
        }
        
        serviceBooking.confirmPickup();
        ServiceBooking saved = serviceBookingRepository.save(serviceBooking);
        
        // Send notification to workshop
        try {
            Workshop workshop = workshopRepository.findById(serviceBooking.getWorkshopId().id()).orElse(null);
            if (workshop != null) {
                User owner = userRepository.findById(workshop.getOwnerUserId().id()).orElse(null);
                if (owner != null) {
                    notificationFacade.notifyPickupConfirmed(command.serviceBookingId(), owner.getEmail());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending pickup confirmed notification", e);
        }
        
        LOGGER.info("Pickup confirmed for service booking {}", command.serviceBookingId());
        return saved;
    }
    
    @Override
    public void handle(CancelServiceBookingCommand command) {
        LOGGER.info("Cancelling service booking {} by user {}", command.serviceBookingId(), command.cancelledBy().id());
        
        ServiceBooking serviceBooking = serviceBookingRepository.findById(command.serviceBookingId())
            .orElseThrow(() -> new ServiceBookingNotFoundException(command.serviceBookingId()));
        
        // Verify ownership (user or workshop can cancel)
        boolean isUser = serviceBooking.getUserId().id().equals(command.cancelledBy().id());
        boolean isWorkshop = serviceBooking.getWorkshopId().id().equals(command.cancelledBy().id());
        
        if (!isUser && !isWorkshop) {
            throw new SecurityException("User does not have access to cancel this service booking");
        }
        
        if (!serviceBooking.canBeCancelled()) {
            throw new IllegalStateException("Service booking cannot be cancelled in current status");
        }
        
        serviceBooking.cancel(command.cancelledBy(), command.cancellationReason());
        serviceBookingRepository.save(serviceBooking);
        LOGGER.info("Service booking {} cancelled successfully", command.serviceBookingId());
    }
}

