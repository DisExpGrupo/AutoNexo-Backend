package com.atg.autonexo.backend.matching.domain.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;

/**
 * Service that maps ServiceCatalog entries to relevant CapabilityTag values.
 * Used for boosting match scores when workshops have capabilities that match requested services.
 */
@Service
public class ServiceCapabilityMappingService {
    
    private final Map<ServiceCatalog, Set<CapabilityTag>> serviceToCapabilityMap;
    
    public ServiceCapabilityMappingService() {
        this.serviceToCapabilityMap = new HashMap<>();
        initializeMappings();
    }
    
    /**
     * Gets capability tags that match a given service.
     * 
     * @param service The service to match
     * @return Set of capability tags that are relevant to this service
     */
    public Set<CapabilityTag> getMatchingCapabilities(ServiceCatalog service) {
        return serviceToCapabilityMap.getOrDefault(service, Set.of());
    }
    
    /**
     * Checks if any of the workshop's capabilities match the requested service.
     * 
     * @param service The requested service
     * @param workshopCapabilities The workshop's capability tags
     * @return true if there's at least one matching capability
     */
    public boolean hasMatchingCapability(ServiceCatalog service, Set<CapabilityTag> workshopCapabilities) {
        Set<CapabilityTag> relevantCapabilities = getMatchingCapabilities(service);
        return workshopCapabilities.stream()
            .anyMatch(relevantCapabilities::contains);
    }
    
    /**
     * Counts how many matching capabilities the workshop has for a set of services.
     * 
     * @param services The requested services
     * @param workshopCapabilities The workshop's capability tags
     * @return Count of matching capabilities
     */
    public int countMatchingCapabilities(Set<ServiceCatalog> services, Set<CapabilityTag> workshopCapabilities) {
        int count = 0;
        for (ServiceCatalog service : services) {
            if (hasMatchingCapability(service, workshopCapabilities)) {
                count++;
            }
        }
        return count;
    }
    
    private void initializeMappings() {
        // ENGINE SERVICES → ENGINE_SPECIALIST, DIAGNOSTICS_SPECIALIST
        addMapping(ServiceCatalog.ENGINE_DIAGNOSTICS, CapabilityTag.ENGINE_SPECIALIST, CapabilityTag.DIAGNOSTICS_SPECIALIST);
        addMapping(ServiceCatalog.SPARK_PLUG_REPLACEMENT, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.TIMING_BELT_REPLACEMENT, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.INJECTOR_CLEANING, CapabilityTag.ENGINE_SPECIALIST, CapabilityTag.DIESEL_SPECIALIST);
        addMapping(ServiceCatalog.ENGINE_TUNEUP, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.ENGINE_OVERHAUL, CapabilityTag.ENGINE_SPECIALIST);
        
        // BRAKE SERVICES → BRAKE_SPECIALIST
        addMapping(ServiceCatalog.BRAKE_PAD_REPLACEMENT, CapabilityTag.BRAKE_SPECIALIST);
        addMapping(ServiceCatalog.BRAKE_DISC_REPLACEMENT, CapabilityTag.BRAKE_SPECIALIST);
        addMapping(ServiceCatalog.BRAKE_FLUID_CHANGE, CapabilityTag.BRAKE_SPECIALIST);
        addMapping(ServiceCatalog.BRAKE_SYSTEM_INSPECTION, CapabilityTag.BRAKE_SPECIALIST);
        
        // TRANSMISSION SERVICES → TRANSMISSION_SPECIALIST
        addMapping(ServiceCatalog.TRANSMISSION_OIL_CHANGE, CapabilityTag.TRANSMISSION_SPECIALIST);
        addMapping(ServiceCatalog.CLUTCH_REPLACEMENT, CapabilityTag.TRANSMISSION_SPECIALIST);
        addMapping(ServiceCatalog.TRANSMISSION_DIAGNOSTICS, CapabilityTag.TRANSMISSION_SPECIALIST, CapabilityTag.DIAGNOSTICS_SPECIALIST);
        
        // SUSPENSION SERVICES → SUSPENSION_SPECIALIST
        addMapping(ServiceCatalog.WHEEL_ALIGNMENT, CapabilityTag.SUSPENSION_SPECIALIST);
        addMapping(ServiceCatalog.WHEEL_BALANCING, CapabilityTag.SUSPENSION_SPECIALIST);
        addMapping(ServiceCatalog.SHOCK_ABSORBER_REPLACEMENT, CapabilityTag.SUSPENSION_SPECIALIST);
        addMapping(ServiceCatalog.SUSPENSION_INSPECTION, CapabilityTag.SUSPENSION_SPECIALIST);
        
        // ELECTRICAL SERVICES → ELECTRICAL_SPECIALIST
        addMapping(ServiceCatalog.BATTERY_REPLACEMENT, CapabilityTag.ELECTRICAL_SPECIALIST);
        addMapping(ServiceCatalog.ALTERNATOR_REPAIR, CapabilityTag.ELECTRICAL_SPECIALIST);
        addMapping(ServiceCatalog.STARTER_REPAIR, CapabilityTag.ELECTRICAL_SPECIALIST);
        addMapping(ServiceCatalog.ELECTRICAL_DIAGNOSTICS, CapabilityTag.ELECTRICAL_SPECIALIST, CapabilityTag.DIAGNOSTICS_SPECIALIST);
        addMapping(ServiceCatalog.HEADLIGHT_RESTORATION, CapabilityTag.ELECTRICAL_SPECIALIST);
        
        // BODYWORK SERVICES → BODYWORK_SPECIALIST, PAINT_SPECIALIST
        addMapping(ServiceCatalog.DENT_REPAIR, CapabilityTag.BODYWORK_SPECIALIST);
        addMapping(ServiceCatalog.PAINT_JOB, CapabilityTag.PAINT_SPECIALIST, CapabilityTag.BODYWORK_SPECIALIST);
        addMapping(ServiceCatalog.SCRATCH_REPAIR, CapabilityTag.BODYWORK_SPECIALIST, CapabilityTag.PAINT_SPECIALIST);
        
        // DIAGNOSTICS SERVICES → DIAGNOSTICS_SPECIALIST
        addMapping(ServiceCatalog.ELECTRONIC_SCAN, CapabilityTag.DIAGNOSTICS_SPECIALIST, CapabilityTag.ELECTRICAL_SPECIALIST);
        addMapping(ServiceCatalog.CHECK_ENGINE_DIAGNOSTICS, CapabilityTag.DIAGNOSTICS_SPECIALIST, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.PRE_PURCHASE_INSPECTION, CapabilityTag.DIAGNOSTICS_SPECIALIST);
        
        // OTHER SERVICES
        addMapping(ServiceCatalog.AC_SERVICE, CapabilityTag.AC_SPECIALIST, CapabilityTag.ELECTRICAL_SPECIALIST);
        
        // COOLING SYSTEM
        addMapping(ServiceCatalog.COOLANT_CHANGE, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.RADIATOR_REPAIR, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.THERMOSTAT_REPLACEMENT, CapabilityTag.ENGINE_SPECIALIST);
        addMapping(ServiceCatalog.WATER_PUMP_REPLACEMENT, CapabilityTag.ENGINE_SPECIALIST);
    }
    
    private void addMapping(ServiceCatalog service, CapabilityTag... capabilities) {
        Set<CapabilityTag> capabilitySet = new HashSet<>();
        for (CapabilityTag capability : capabilities) {
            capabilitySet.add(capability);
        }
        serviceToCapabilityMap.put(service, capabilitySet);
    }
}

