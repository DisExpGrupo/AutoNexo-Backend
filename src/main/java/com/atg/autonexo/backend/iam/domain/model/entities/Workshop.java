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
@Table(name = "workshops_reference")
public class Workshop extends AuditableModel {
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "workshop_id", unique = true, nullable = false))
    private WorkshopId workshopId;

    protected Workshop() {
        // Required by JPA 
    }
    
    public Workshop(Long workshopId) {
        this.workshopId = new WorkshopId(workshopId);
    }
    
}