package com.atg.autonexo.backend.workshop.domain.model.entities;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Address;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.OpeningHours;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Location entity representing a physical workshop location.
 * Part of the Workshop aggregate.
 */
@Entity
@Getter
@Setter
public class Location extends AuditableModel {
    
    @Embedded
    private Address address;
    
    @Embedded
    private Coordinates coordinates;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "location_opening_hours", joinColumns = @JoinColumn(name = "location_id"))
    private List<OpeningHours> openingHours = new ArrayList<>();
    
    @Column(nullable = false)
    private boolean active = true;
    
    protected Location() {}
    
    public Location(Address address, Coordinates coordinates) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        this.address = address;
        this.coordinates = coordinates;
        this.openingHours = new ArrayList<>();
        this.active = true;
    }
    
    /**
     * Adds opening hours for a specific day
     */
    public void addOpeningHours(OpeningHours hours) {
        if (hours != null && !this.openingHours.contains(hours)) {
            this.openingHours.add(hours);
        }
    }
    
    /**
     * Deactivates this location
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Activates this location
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Updates the address of this location
     */
    public void updateAddress(Address newAddress) {
        if (newAddress == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        this.address = newAddress;
    }
    
    /**
     * Updates the coordinates of this location
     */
    public void updateCoordinates(Coordinates newCoordinates) {
        this.coordinates = newCoordinates;
    }
    
    /**
     * Updates location details
     */
    public void update(String name, Address address, Coordinates coordinates, boolean isPrimary) {
        // Note: name and isPrimary are currently not stored in Location entity
        // This method is here for compatibility with UpdateLocationCommand
        if (address != null) {
            this.address = address;
        }
        if (coordinates != null) {
            this.coordinates = coordinates;
        }
    }
}

