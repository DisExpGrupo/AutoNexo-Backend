package com.atg.autonexo.backend.iam.domain.model.aggregates;

import java.util.ArrayList;
import java.util.List;

import com.atg.autonexo.backend.iam.domain.model.entities.Role;
import com.atg.autonexo.backend.iam.domain.model.entities.Workshop;
import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/** 
 * User aggregate root for IAM context
 * This class represents the aggregate root for the User entity with multitenancy support.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = true)
    private String phoneNumber;
    
    @Column(nullable = false)
    private boolean isVerified = false;
    
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    // Relación Opcional con Taller 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workshop_id", nullable = true)
    private Workshop workshop;

    protected User() {
        this.roles = new ArrayList<>();
    }

    /**
     * Constructor for the User aggregate root
     * @param userId the user ID
     * @param businessId the business ID
     * @param email the email
     * @param passwordHash the password hash
     * @param firstName the first name
     * @param lastName the last name
     * @param phoneNumber the phone number
     * @param isVerified the is verified
     */
    public User(String email, String passwordHash, String firstName, String lastName, String phoneNumber, boolean isVerified) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isVerified = isVerified;
        this.roles = new ArrayList<>();
    }
    
    public void addRole(Role role) {
        if (role != null && !this.roles.contains(role)) {
            this.roles.add(role);
        }
    }
    
    public String getFullName() {
        return String.format("%s %s", firstName, lastName).trim();
    }
}
