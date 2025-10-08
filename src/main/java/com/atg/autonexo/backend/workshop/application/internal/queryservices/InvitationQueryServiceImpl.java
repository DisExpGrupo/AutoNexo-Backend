package com.atg.autonexo.backend.workshop.application.internal.queryservices;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetInvitationByCodeQuery;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetInvitationsByWorkshopQuery;
import com.atg.autonexo.backend.workshop.domain.services.InvitationQueryService;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.InvitationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of Invitation Query Service.
 * Handles all read operations for invitations.
 */
@Service
@Transactional(readOnly = true)
public class InvitationQueryServiceImpl implements InvitationQueryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationQueryServiceImpl.class);
    
    private final InvitationRepository invitationRepository;
    
    public InvitationQueryServiceImpl(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }
    
    @Override
    public Optional<Invitation> handle(GetInvitationByCodeQuery query) {
        LOGGER.debug("Fetching invitation by code: {}", query.code());
        
        try {
            return invitationRepository.findByCode(query.code());
        } catch (Exception e) {
            LOGGER.error("Error fetching invitation by code: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<Invitation> handle(GetInvitationsByWorkshopQuery query) {
        LOGGER.debug("Fetching invitations for workshop ID: {}", query.workshopId());
        
        try {
            return invitationRepository.findByWorkshopId(query.workshopId());
        } catch (Exception e) {
            LOGGER.error("Error fetching invitations for workshop: {}", e.getMessage(), e);
            return List.of();
        }
    }
}

