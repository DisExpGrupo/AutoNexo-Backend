package com.atg.autonexo.backend.workshop.domain.model.aggregates;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Email;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.InvitationCode;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Invitation aggregate root for staff member invitations.
 * Allows workshops to invite employees via email with expiring codes.
 */
@Entity
@Getter
@Setter
public class Invitation extends AuditableAbstractAggregateRoot<Invitation> {
    
    @Embedded
    private InvitationCode invitationCode;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Embedded
    private Email deliveredTo;
    
    @Embedded
    private WorkshopId workshopId;
    
    @Column(length = 500)
    private String message;
    
    protected Invitation() {}
    
    /**
     * Constructor for creating a new invitation
     */
    public Invitation(InvitationCode invitationCode, LocalDateTime expiresAt, 
                      Email deliveredTo, WorkshopId workshopId, String message) {
        if (invitationCode == null) {
            throw new IllegalArgumentException("Invitation code cannot be null");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("Workshop ID cannot be null");
        }
        
        this.invitationCode = invitationCode;
        this.expiresAt = expiresAt;
        this.used = false;
        this.deliveredTo = deliveredTo;
        this.workshopId = workshopId;
        this.message = message;
    }
    
    /**
     * Checks if the invitation is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    /**
     * Checks if the invitation can be used
     */
    public boolean canBeUsed() {
        return !this.used && !isExpired();
    }
    
    /**
     * Marks the invitation as used
     */
    public void markAsUsed() {
        if (this.used) {
            throw new IllegalStateException("Invitation has already been used");
        }
        if (isExpired()) {
            throw new IllegalStateException("Invitation has expired");
        }
        this.used = true;
    }
    
    /**
     * Checks if this invitation belongs to a specific workshop
     */
    public boolean belongsToWorkshop(Long workshopId) {
        return workshopId != null && this.workshopId.id().equals(workshopId);
    }
}
