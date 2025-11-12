package com.atg.autonexo.backend.workshop.domain.model.aggregates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;
import com.atg.autonexo.backend.workshop.domain.exceptions.LocationNotFoundException;
import com.atg.autonexo.backend.workshop.domain.exceptions.ServiceTemplateNotFoundException;
import com.atg.autonexo.backend.workshop.domain.exceptions.StaffMemberNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.entities.Location;
import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.BusinessRegistration;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Workshop aggregate root representing a vehicle repair workshop.
 * Manages locations, staff, service templates, and capabilities.
 */
@Entity
@Getter
@Setter
public class Workshop extends AuditableAbstractAggregateRoot<Workshop> {
    
    @Embedded
    private UserId ownerUserId;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 500)
    private String shortDescription;
    
    @Column(length = 300)
    private String legalName;
    
    @Embedded
    private BusinessRegistration businessRegistration;
    
    @Column
    private Float trustScore;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column
    private LocalDateTime deletedAt;
    
    @Column(length = 500)
    private String logoUrl;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "workshop_photos", joinColumns = @JoinColumn(name = "workshop_id"))
    @Column(name = "photo_url", length = 500)
    private List<String> photoUrls = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workshop_id")
    private List<Location> locations = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workshop_id")
    private List<ServiceTemplate> serviceTemplates = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workshop_id")
    private List<StaffMember> staffMembers = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "workshop_capability_tags", joinColumns = @JoinColumn(name = "workshop_id"))
    @Column(name = "tag")
    private Set<CapabilityTag> capabilityTags = new HashSet<>();
    
    // === Subscription Management ===
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;
    
    @Column
    private LocalDateTime subscriptionExpiresAt;
    
    protected Workshop() {}
    
    /**
     * Constructor for creating a new workshop
     */
    public Workshop(UserId ownerUserId, String name, String shortDescription, 
                    String legalName, BusinessRegistration businessRegistration) {
        if (ownerUserId == null) {
            throw new IllegalArgumentException("Owner user ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Workshop name cannot be null or blank");
        }
        
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.legalName = legalName;
        this.businessRegistration = businessRegistration;
        this.active = true;
        this.locations = new ArrayList<>();
        this.serviceTemplates = new ArrayList<>();
        this.staffMembers = new ArrayList<>();
        this.capabilityTags = new HashSet<>();
        this.photoUrls = new ArrayList<>();
        this.subscriptionStatus = SubscriptionStatus.TRIAL;
        this.subscriptionTier = SubscriptionTier.FREE;
    }
    
    // === Workshop Management ===
    
    /**
     * Checks if this workshop is owned by a specific user
     */
    public boolean isOwnedBy(Long userId) {
        return userId != null && this.ownerUserId.id().equals(userId);
    }
    
    /**
     * Updates basic workshop information
     */
    public void updateBasicInfo(String name, String shortDescription, String legalName) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (shortDescription != null) {
            this.shortDescription = shortDescription;
        }
        if (legalName != null) {
            this.legalName = legalName;
        }
    }
    
    /**
     * Updates business registration
     */
    public void updateBusinessRegistration(BusinessRegistration newRegistration) {
        this.businessRegistration = newRegistration;
    }
    
    /**
     * Updates trust score
     */
    public void updateTrustScore(Float newScore) {
        if (newScore != null && newScore >= 0 && newScore <= 5) {
            this.trustScore = newScore;
        }
    }
    
    /**
     * Deactivates the workshop (soft delete)
     */
    public void deactivate() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }
    
    /**
     * Activates the workshop
     */
    public void activate() {
        this.active = true;
        this.deletedAt = null;
    }
    
    // === Location Management ===
    
    /**
     * Adds a new location to the workshop
     */
    public Location addLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.locations.add(location);
        return location;
    }
    
    /**
     * Finds a location by ID
     */
    public Optional<Location> findLocationById(Long locationId) {
        return this.locations.stream()
            .filter(loc -> loc.getId().equals(locationId))
            .findFirst();
    }
    
    /**
     * Removes a location
     */
    public void removeLocation(Long locationId) {
        Location location = findLocationById(locationId)
            .orElseThrow(() -> new LocationNotFoundException(locationId));
        this.locations.remove(location);
    }
    
    /**
     * Gets all active locations
     */
    public List<Location> getActiveLocations() {
        return this.locations.stream()
            .filter(Location::isActive)
            .toList();
    }
    
    // === Staff Management ===
    
    /**
     * Adds a new staff member to the workshop
     */
    public StaffMember addStaffMember(StaffMember staffMember) {
        if (staffMember == null) {
            throw new IllegalArgumentException("Staff member cannot be null");
        }
        this.staffMembers.add(staffMember);
        return staffMember;
    }
    
    /**
     * Finds a staff member by ID
     */
    public Optional<StaffMember> findStaffMemberById(Long staffMemberId) {
        return this.staffMembers.stream()
            .filter(staff -> staff.getId().equals(staffMemberId))
            .findFirst();
    }
    
    /**
     * Finds staff member by user ID
     */
    public Optional<StaffMember> findStaffMemberByUserId(Long userId) {
        return this.staffMembers.stream()
            .filter(staff -> staff.belongsToUser(userId))
            .findFirst();
    }
    
    /**
     * Removes a staff member
     */
    public void removeStaffMember(Long staffMemberId) {
        StaffMember staff = findStaffMemberById(staffMemberId)
            .orElseThrow(() -> new StaffMemberNotFoundException(staffMemberId));
        this.staffMembers.remove(staff);
    }
    
    /**
     * Gets all active staff members
     */
    public List<StaffMember> getActiveStaffMembers() {
        return this.staffMembers.stream()
            .filter(StaffMember::isActive)
            .toList();
    }
    
    // === Service Template Management ===
    
    /**
     * Adds a new service template to the workshop
     */
    public ServiceTemplate addServiceTemplate(ServiceTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("Service template cannot be null");
        }
        this.serviceTemplates.add(template);
        return template;
    }
    
    /**
     * Finds a service template by ID
     */
    public Optional<ServiceTemplate> findServiceTemplateById(Long templateId) {
        return this.serviceTemplates.stream()
            .filter(tpl -> tpl.getId().equals(templateId))
            .findFirst();
    }
    
    /**
     * Removes a service template
     */
    public void removeServiceTemplate(Long templateId) {
        ServiceTemplate template = findServiceTemplateById(templateId)
            .orElseThrow(() -> new ServiceTemplateNotFoundException(templateId));
        this.serviceTemplates.remove(template);
    }
    
    /**
     * Gets all active service templates
     */
    public List<ServiceTemplate> getActiveServiceTemplates() {
        return this.serviceTemplates.stream()
            .filter(ServiceTemplate::isActive)
            .toList();
    }
    
    // === Capability Tag Management ===
    
    /**
     * Adds a capability tag to the workshop
     */
    public void addCapabilityTag(CapabilityTag tag) {
        if (tag != null) {
            this.capabilityTags.add(tag);
        }
    }
    
    /**
     * Removes a capability tag from the workshop
     */
    public void removeCapabilityTag(CapabilityTag tag) {
        if (tag != null) {
            this.capabilityTags.remove(tag);
        }
    }
    
    /**
     * Checks if the workshop has a specific capability tag
     */
    public boolean hasCapabilityTag(CapabilityTag tag) {
        return tag != null && this.capabilityTags.contains(tag);
    }
    
    /**
     * Gets all capability tags
     */
    public Set<CapabilityTag> getCapabilityTags() {
        return new HashSet<>(this.capabilityTags);
    }
    
    /**
     * Clears all capability tags
     */
    public void clearCapabilityTags() {
        this.capabilityTags.clear();
    }
    
    /**
     * Sets multiple capability tags at once
     */
    public void setCapabilityTags(Set<CapabilityTag> tags) {
        this.capabilityTags.clear();
        if (tags != null) {
            this.capabilityTags.addAll(tags);
        }
    }
    
    // === Media Management ===
    
    /**
     * Sets the workshop logo URL
     */
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    /**
     * Adds a photo URL to the workshop carousel
     */
    public void addPhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            if (this.photoUrls.size() >= 10) {
                throw new IllegalStateException("Maximum of 10 photos allowed");
            }
            this.photoUrls.add(photoUrl);
        }
    }
    
    /**
     * Removes a photo URL from the workshop carousel
     */
    public void removePhoto(String photoUrl) {
        if (photoUrl != null) {
            this.photoUrls.remove(photoUrl);
        }
    }
    
    /**
     * Removes a photo by index
     */
    public void removePhotoByIndex(int index) {
        if (index >= 0 && index < this.photoUrls.size()) {
            this.photoUrls.remove(index);
        }
    }
    
    /**
     * Gets all photo URLs
     */
    public List<String> getPhotoUrls() {
        return new ArrayList<>(this.photoUrls);
    }
    
    // === Subscription Management ===
    
    /**
     * Updates the workshop subscription
     */
    public void updateSubscription(SubscriptionStatus status, SubscriptionTier tier, LocalDateTime expiresAt) {
        if (status == null) {
            throw new IllegalArgumentException("Subscription status cannot be null");
        }
        if (tier == null) {
            throw new IllegalArgumentException("Subscription tier cannot be null");
        }
        this.subscriptionStatus = status;
        this.subscriptionTier = tier;
        this.subscriptionExpiresAt = expiresAt;
    }
    
    /**
     * Checks if the subscription is currently active
     */
    public boolean isSubscriptionActive() {
        if (subscriptionStatus == SubscriptionStatus.CANCELLED || 
            subscriptionStatus == SubscriptionStatus.EXPIRED) {
            return false;
        }
        
        if (subscriptionExpiresAt != null && LocalDateTime.now().isAfter(subscriptionExpiresAt)) {
            return false;
        }
        
        return subscriptionStatus == SubscriptionStatus.ACTIVE || 
               subscriptionStatus == SubscriptionStatus.TRIAL;
    }
    
    /**
     * Checks if the workshop can access premium features
     */
    public boolean canAccessPremiumFeatures() {
        return isSubscriptionActive() && 
               (subscriptionTier == SubscriptionTier.BASIC || subscriptionTier == SubscriptionTier.PREMIUM);
    }
}