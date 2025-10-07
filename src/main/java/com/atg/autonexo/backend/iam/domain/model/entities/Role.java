package com.atg.autonexo.backend.iam.domain.model.entities;

import com.atg.autonexo.backend.iam.domain.model.valueobjects.Roles;
import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Entity
@Getter
public class Role extends AuditableModel {

    @Enumerated(EnumType.STRING)
    @Column(length = 30, unique = true,nullable = false)
    private Roles name;

    protected Role() { }

    public Role(Roles name) { this.name = name; }

    public static Role from(String name) {
        return new Role(Roles.valueOf(name.toUpperCase()));
    }

}
