package com.atg.autonexo.backend.iam.domain.model.entities;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
/**
 * Workshop Reference Entity for the IAM Bounded Context.
 * Its purpose is solely to maintain the relationship  between 
 * the User and the Workshop's unique business ID (WorkshopId) 
 */
@Entity
@Getter
@Setter
@Table(name = "iam_workshop_references")
public class WorkshopReference extends AuditableModel {
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "workshop_id", unique = true, nullable = false))
    private WorkshopId workshopId;

    protected WorkshopReference() {
        // Required by JPA 
    }
    
    public WorkshopReference(Long workshopId) {
        this.workshopId = new WorkshopId(workshopId);
    }
    
    /**
     * Checks if this reference is for the given workshop ID
     */
    public boolean isForWorkshop(Long workshopId) {
        return workshopId != null && this.workshopId != null && 
               this.workshopId.id().equals(workshopId);
    }
    
}
