package com.atg.autonexo.backend.workshop.application.internal.queryservices;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.queries.*;
import com.atg.autonexo.backend.workshop.domain.services.WorkshopQueryService;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of Workshop Query Service.
 * Handles all read operations for the Workshop aggregate.
 */
@Service
@Transactional(readOnly = true)
public class WorkshopQueryServiceImpl implements WorkshopQueryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkshopQueryServiceImpl.class);
    
    private final WorkshopRepository workshopRepository;
    
    public WorkshopQueryServiceImpl(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }
    
    @Override
    public Optional<Workshop> handle(GetWorkshopByIdQuery query) {
        LOGGER.info("Fetching workshop by ID: {}", query.workshopId());
        
        try {
            return workshopRepository.findById(query.workshopId());
        } catch (Exception e) {
            LOGGER.error("Error fetching workshop by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Workshop> handle(GetWorkshopByOwnerQuery query) {
        LOGGER.info("Fetching workshop by owner user ID: {}", query.ownerUserId());
        
        try {
            return workshopRepository.findByOwnerUserId(query.ownerUserId());
        } catch (Exception e) {
            LOGGER.error("Error fetching workshop by owner: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<Workshop> handle(GetAllWorkshopsQuery query) {
        LOGGER.info("Fetching all active workshops");
        
        try {
            return workshopRepository.findByActiveTrue();
        } catch (Exception e) {
            LOGGER.error("Error fetching all workshops: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<Workshop> handle(GetWorkshopsByCapabilityTagQuery query) {
        LOGGER.info("Fetching workshops by capability tag: {}", query.tag());
        
        try {
            return workshopRepository.findByCapabilityTag(query.tag());
        } catch (Exception e) {
            LOGGER.error("Error fetching workshops by capability tag: {}", e.getMessage(), e);
            return List.of();
        }
    }
}

