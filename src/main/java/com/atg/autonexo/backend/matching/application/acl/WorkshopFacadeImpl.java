package com.atg.autonexo.backend.matching.application.acl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.interfaces.acl.WorkshopFacade;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.entities.Location;
import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

/**
 * Implementation of WorkshopFacade.
 * Provides ACL for Matching & Booking context to access Workshop context data.
 */
@Service
@Transactional(readOnly = true)
public class WorkshopFacadeImpl implements WorkshopFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkshopFacadeImpl.class);
    
    private final WorkshopRepository workshopRepository;
    
    public WorkshopFacadeImpl(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }
    
    @Override
    public WorkshopInfo getWorkshopInfo(WorkshopId workshopId) {
        Workshop workshop = workshopRepository.findById(workshopId.id())
            .orElseThrow(() -> new WorkshopNotFoundException(workshopId.id()));
        
        Coordinates primaryLocation = null;
        List<Location> locations = workshop.getLocations();
        if (locations != null && !locations.isEmpty()) {
            Location firstActiveLocation = locations.stream()
                .filter(Location::isActive)
                .findFirst()
                .orElse(locations.get(0));
            primaryLocation = firstActiveLocation.getCoordinates();
        }
        
        return new WorkshopInfo(
            workshopId,
            workshop.getName(),
            primaryLocation,
            workshop.getTrustScore() != null ? workshop.getTrustScore().doubleValue() : null,
            workshop.isActive()
        );
    }
    
    @Override
    public List<LocationInfo> getWorkshopLocations(WorkshopId workshopId) {
        Workshop workshop = workshopRepository.findById(workshopId.id())
            .orElseThrow(() -> new WorkshopNotFoundException(workshopId.id()));
        
        List<LocationInfo> locationInfos = new ArrayList<>();
        List<Location> locations = workshop.getLocations();
        
        if (locations != null) {
            for (Location location : locations) {
                locationInfos.add(new LocationInfo(
                    location.getId(),
                    location.getCoordinates(),
                    location.isActive()
                ));
            }
        }
        
        return locationInfos;
    }
    
    @Override
    public List<ServiceCatalog> getWorkshopServices(WorkshopId workshopId) {
        Workshop workshop = workshopRepository.findById(workshopId.id())
            .orElseThrow(() -> new WorkshopNotFoundException(workshopId.id()));
        
        List<ServiceTemplate> serviceTemplates = workshop.getServiceTemplates();
        if (serviceTemplates == null) {
            return new ArrayList<>();
        }
        
        return serviceTemplates.stream()
            .filter(ServiceTemplate::isActive)
            .filter(ServiceTemplate::isLinkedToCatalog)
            .map(ServiceTemplate::getCatalogService)
            .distinct()
            .collect(Collectors.toList());
    }
    
    @Override
    public Double getWorkshopRating(WorkshopId workshopId) {
        Workshop workshop = workshopRepository.findById(workshopId.id())
            .orElseThrow(() -> new WorkshopNotFoundException(workshopId.id()));
        
        return workshop.getTrustScore() != null ? workshop.getTrustScore().doubleValue() : null;
    }
    
    @Override
    public List<WorkshopInfo> getAllActiveWorkshops() {
        List<Workshop> workshops = workshopRepository.findByActiveTrue();
        
        return workshops.stream()
            .map(w -> {
                Coordinates primaryLocation = null;
                List<Location> locations = w.getLocations();
                if (locations != null && !locations.isEmpty()) {
                    Location firstActiveLocation = locations.stream()
                        .filter(Location::isActive)
                        .findFirst()
                        .orElse(locations.get(0));
                    primaryLocation = firstActiveLocation.getCoordinates();
                }
                
                return new WorkshopInfo(
                    new WorkshopId(w.getId()),
                    w.getName(),
                    primaryLocation,
                    w.getTrustScore() != null ? w.getTrustScore().doubleValue() : null,
                    w.isActive()
                );
            })
            .collect(Collectors.toList());
    }
}

