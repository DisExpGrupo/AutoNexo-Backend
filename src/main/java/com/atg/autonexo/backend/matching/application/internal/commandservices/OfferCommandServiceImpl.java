package com.atg.autonexo.backend.matching.application.internal.commandservices;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.exceptions.OfferNotFoundException;
import com.atg.autonexo.backend.matching.domain.exceptions.InvalidOfferStatusException;
import com.atg.autonexo.backend.matching.domain.exceptions.ServiceRequestNotFoundException;
import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceBooking;
import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.domain.model.commands.AcceptOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.CreateOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.RejectOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.WithdrawOfferCommand;
import com.atg.autonexo.backend.matching.domain.services.OfferCommandService;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.OfferRepository;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceBookingRepository;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceRequestRepository;
import com.atg.autonexo.backend.matching.interfaces.acl.NotificationFacade;
import com.atg.autonexo.backend.matching.interfaces.acl.WorkshopFacade;
import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

/**
 * Implementation of OfferCommandService.
 */
@Service
@Transactional
public class OfferCommandServiceImpl implements OfferCommandService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferCommandServiceImpl.class);
    
    private final OfferRepository offerRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final NotificationFacade notificationFacade;
    private final UserRepository userRepository;
    private final WorkshopRepository workshopRepository;
    private final WorkshopFacade workshopFacade;
    
    public OfferCommandServiceImpl(
            OfferRepository offerRepository,
            ServiceRequestRepository serviceRequestRepository,
            ServiceBookingRepository serviceBookingRepository,
            NotificationFacade notificationFacade,
            UserRepository userRepository,
            WorkshopRepository workshopRepository,
            WorkshopFacade workshopFacade) {
        this.offerRepository = offerRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceBookingRepository = serviceBookingRepository;
        this.notificationFacade = notificationFacade;
        this.userRepository = userRepository;
        this.workshopRepository = workshopRepository;
        this.workshopFacade = workshopFacade;
    }
    
    @Override
    public Offer handle(CreateOfferCommand command) {
        LOGGER.info("Creating offer for service request {} by workshop {}", command.serviceRequestId(), command.workshopId().id());
        
        // Verify service request exists and is pending
        ServiceRequest serviceRequest = serviceRequestRepository.findById(command.serviceRequestId())
            .orElseThrow(() -> new ServiceRequestNotFoundException(command.serviceRequestId()));
        
        if (!serviceRequest.canAcceptOffers()) {
            throw new IllegalStateException("Service request cannot accept offers in current status");
        }
        
        // Check if workshop has already rejected this request
        if (serviceRequest.isRejectedByWorkshop(command.workshopId())) {
            throw new IllegalStateException("Workshop has already rejected this service request");
        }
        
        // Calculate expiration (3 days from now)
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);
        
        // Create offer
        Offer offer = new Offer(
            command.serviceRequestId(),
            command.workshopId(),
            command.proposedPrice(),
            command.proposedDate(),
            command.message(),
            expiresAt
        );
        
        // Add offer to service request
        serviceRequest.addOffer(offer);
        
        Offer savedOffer = offerRepository.save(offer);
        serviceRequestRepository.save(serviceRequest);
        
        // Send notification to user
        try {
            User user = userRepository.findById(serviceRequest.getUserId().id()).orElse(null);
            if (user != null) {
                notificationFacade.notifyOfferReceived(serviceRequest.getId(), savedOffer.getId(), user.getEmail());
            }
        } catch (Exception e) {
            LOGGER.error("Error sending offer received notification", e);
        }
        
        LOGGER.info("Offer created successfully with ID: {}", savedOffer.getId());
        return savedOffer;
    }
    
    @Override
    public void handle(WithdrawOfferCommand command) {
        LOGGER.info("Withdrawing offer {} by workshop {}", command.offerId(), command.workshopId().id());
        
        Offer offer = offerRepository.findById(command.offerId())
            .orElseThrow(() -> new OfferNotFoundException(command.offerId()));
        
        // Verify ownership
        if (!offer.getWorkshopId().id().equals(command.workshopId().id())) {
            throw new SecurityException("Workshop does not own this offer");
        }
        
        if (!offer.canBeWithdrawn()) {
            throw new InvalidOfferStatusException(command.offerId(), offer.getStatus(), "withdraw");
        }
        
        offer.withdraw();
        offerRepository.save(offer);
        LOGGER.info("Offer {} withdrawn successfully", command.offerId());
    }
    
    @Override
    public ServiceBooking handle(AcceptOfferCommand command) {
        LOGGER.info("Accepting offer {} by user {}", command.offerId(), command.userId());
        
        Offer offer = offerRepository.findById(command.offerId())
            .orElseThrow(() -> new OfferNotFoundException(command.offerId()));
        
        if (!offer.canBeAccepted()) {
            throw new InvalidOfferStatusException(command.offerId(), offer.getStatus(), "accept");
        }
        
        // Get service request
        ServiceRequest serviceRequest = serviceRequestRepository.findById(offer.getServiceRequestId())
            .orElseThrow(() -> new ServiceRequestNotFoundException(offer.getServiceRequestId()));
        
        // Verify ownership
        if (!serviceRequest.getUserId().id().equals(command.userId())) {
            throw new SecurityException("User does not own this service request");
        }
        
        // Accept the offer
        offer.accept();
        offerRepository.save(offer);
        
        // Reject all other pending offers for this service request
        serviceRequest.getOffers().stream()
            .filter(o -> o.getId() != null && !o.getId().equals(offer.getId()))
            .filter(o -> o.getStatus() == com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus.PENDING)
            .forEach(o -> {
                o.reject();
                offerRepository.save(o);
            });
        
        // Mark service request as completed
        serviceRequest.markAsCompleted();
        serviceRequestRepository.save(serviceRequest);
        
        // Create ServiceBooking
        ServiceBooking serviceBooking = new ServiceBooking(
            serviceRequest.getId(),
            offer.getId(),
            serviceRequest.getUserId(),
            serviceRequest.getVehicleId(),
            offer.getWorkshopId(),
            offer.getProposedDate(),
            offer.getProposedPrice(),
            serviceRequest.getRequestedServices(),
            serviceRequest.getDescription()
        );
        
        ServiceBooking savedBooking = serviceBookingRepository.save(serviceBooking);
        
        // Send notification to workshop
        try {
            Workshop workshop = workshopRepository.findById(offer.getWorkshopId().id()).orElse(null);
            if (workshop != null) {
                User owner = userRepository.findById(workshop.getOwnerUserId().id()).orElse(null);
                if (owner != null) {
                    notificationFacade.notifyOfferAccepted(offer.getId(), owner.getEmail());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending offer accepted notification", e);
        }
        
        LOGGER.info("Service booking created successfully with ID: {} from offer {}", savedBooking.getId(), command.offerId());
        
        return savedBooking;
    }
    
    @Override
    public void handle(RejectOfferCommand command) {
        LOGGER.info("Rejecting offer {} by user {}", command.offerId(), command.userId());
        
        Offer offer = offerRepository.findById(command.offerId())
            .orElseThrow(() -> new OfferNotFoundException(command.offerId()));
        
        // Get service request to verify ownership
        ServiceRequest serviceRequest = serviceRequestRepository.findById(offer.getServiceRequestId())
            .orElseThrow(() -> new ServiceRequestNotFoundException(offer.getServiceRequestId()));
        
        // Verify ownership
        if (!serviceRequest.getUserId().id().equals(command.userId())) {
            throw new SecurityException("User does not own this service request");
        }
        
        if (offer.getStatus() != com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus.PENDING) {
            throw new InvalidOfferStatusException(command.offerId(), offer.getStatus(), "reject");
        }
        
        offer.reject();
        offerRepository.save(offer);
        
        // Send notification to workshop
        try {
            Workshop workshop = workshopRepository.findById(offer.getWorkshopId().id()).orElse(null);
            if (workshop != null) {
                User owner = userRepository.findById(workshop.getOwnerUserId().id()).orElse(null);
                if (owner != null) {
                    notificationFacade.notifyOfferRejected(offer.getId(), owner.getEmail());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending offer rejected notification", e);
        }
        
        LOGGER.info("Offer {} rejected successfully", command.offerId());
    }
}

