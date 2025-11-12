package com.atg.autonexo.backend.vehicle.domain.model.aggregates;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.vehicle.domain.model.entities.ServicePerformed;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.MaintenanceStatus;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.Mileage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Maintenance aggregate root.
 * Represents a maintenance record with services performed, costs, and images.
 */
@Entity
@Getter
@Setter
public class Maintenance extends AuditableAbstractAggregateRoot<Maintenance> {
    
    @Column(nullable = false)
    private Long vehicleId;
    
    @Column(nullable = false)
    private LocalDate maintenanceDate;
    
    @Embedded
    private Mileage mileage;
    
    @Embedded
    private WorkshopId workshopId; // Optional - null for manual entries
    
    @Column(nullable = false)
    private boolean createdByWorkshop = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MaintenanceStatus status;
    
    @Column(length = 1000)
    private String observations;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
    @OneToMany(mappedBy = "maintenanceId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServicePerformed> servicesPerformed = new ArrayList<>();
    
    protected Maintenance() {}
    
    /**
     * Creates a manual maintenance record (immediately confirmed).
     */
    public Maintenance(Long vehicleId, LocalDate maintenanceDate, Mileage mileage, 
                      String observations, WorkshopId workshopId) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (maintenanceDate == null) {
            throw new IllegalArgumentException("Maintenance date cannot be null");
        }
        if (mileage == null) {
            throw new IllegalArgumentException("Mileage cannot be null");
        }
        
        this.vehicleId = vehicleId;
        this.maintenanceDate = maintenanceDate;
        this.mileage = mileage;
        this.workshopId = workshopId;
        this.createdByWorkshop = false;
        this.status = MaintenanceStatus.MANUAL;
        this.observations = observations;
    }
    
    /**
     * Creates a workshop-created maintenance record (pending confirmation).
     */
    public static Maintenance createFromWorkshop(Long vehicleId, LocalDate maintenanceDate, 
                                                Mileage mileage, WorkshopId workshopId, 
                                                String observations) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (maintenanceDate == null) {
            throw new IllegalArgumentException("Maintenance date cannot be null");
        }
        if (mileage == null) {
            throw new IllegalArgumentException("Mileage cannot be null");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null for workshop-created maintenance");
        }
        
        Maintenance maintenance = new Maintenance();
        maintenance.vehicleId = vehicleId;
        maintenance.maintenanceDate = maintenanceDate;
        maintenance.mileage = mileage;
        maintenance.workshopId = workshopId;
        maintenance.createdByWorkshop = true;
        maintenance.status = MaintenanceStatus.PENDING_CONFIRMATION;
        maintenance.observations = observations;
        
        return maintenance;
    }
    
    /**
     * Adds a service performed during this maintenance.
     */
    public void addService(ServiceCatalog serviceType, String description, BigDecimal cost) {
        if (serviceType == null) {
            throw new IllegalArgumentException("ServiceType cannot be null");
        }
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost cannot be null or negative");
        }
        if (isConfirmed()) {
            throw new IllegalStateException("Cannot modify confirmed maintenance");
        }
        
        // Create service - maintenanceId will be set after saving via updateServicesMaintenanceId()
        ServicePerformed service = new ServicePerformed(serviceType, description, cost, 
            this.getId()); // Can be null initially
        this.servicesPerformed.add(service);
    }
    
    /**
     * Updates maintenance ID for all services after persistence.
     * Called by JPA lifecycle callbacks or service layer after save.
     */
    public void updateServicesMaintenanceId() {
        if (this.getId() != null) {
            for (ServicePerformed service : this.servicesPerformed) {
                if (service.getMaintenanceId() == null || service.getMaintenanceId() == 0L) {
                    service.setMaintenanceId(this.getId());
                }
            }
        }
    }
    
    /**
     * Confirms a workshop-created maintenance.
     */
    public void confirm() {
        if (!this.createdByWorkshop) {
            throw new IllegalStateException("Only workshop-created maintenance can be confirmed");
        }
        if (this.status != MaintenanceStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Only pending maintenance can be confirmed");
        }
        this.status = MaintenanceStatus.CONFIRMED;
    }
    
    /**
     * Rejects a workshop-created maintenance.
     */
    public void reject() {
        if (!this.createdByWorkshop) {
            throw new IllegalStateException("Only workshop-created maintenance can be rejected");
        }
        if (this.status != MaintenanceStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Only pending maintenance can be rejected");
        }
        this.status = MaintenanceStatus.REJECTED;
    }
    
    /**
     * Adds an image URL to the maintenance.
     */
    public void addImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (isConfirmed()) {
            throw new IllegalStateException("Cannot modify confirmed maintenance");
        }
        if (!this.imageUrls.contains(imageUrl)) {
            this.imageUrls.add(imageUrl);
        }
    }
    
    /**
     * Removes an image URL from the maintenance.
     */
    public void removeImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (isConfirmed()) {
            throw new IllegalStateException("Cannot modify confirmed maintenance");
        }
        this.imageUrls.remove(imageUrl);
    }
    
    /**
     * Calculates the total cost of all services performed.
     */
    public BigDecimal getTotalCost() {
        return servicesPerformed.stream()
            .map(ServicePerformed::getCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Checks if maintenance is confirmed.
     */
    public boolean isConfirmed() {
        return status == MaintenanceStatus.CONFIRMED || status == MaintenanceStatus.MANUAL;
    }
    
    /**
     * Checks if maintenance is pending confirmation.
     */
    public boolean isPendingConfirmation() {
        return status == MaintenanceStatus.PENDING_CONFIRMATION;
    }
    
    /**
     * Checks if maintenance can be modified.
     */
    public boolean canBeModified() {
        return !isConfirmed() && status != MaintenanceStatus.REJECTED;
    }
}

