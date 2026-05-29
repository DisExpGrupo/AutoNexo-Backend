package com.atg.autonexo.backend.shared.infrastructure.actuator;

import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories.VehicleBrandRepository;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class StartupReadinessHealthIndicator implements HealthIndicator {

    private final RoleRepository roleRepository;
    private final VehicleBrandRepository brandRepository;

    public StartupReadinessHealthIndicator(RoleRepository roleRepository,
                                           VehicleBrandRepository brandRepository) {
        this.roleRepository = roleRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    public Health health() {
        long roleCount = roleRepository.count();
        long brandCount = brandRepository.count();

        boolean rolesSeeded = roleCount >= 4;
        boolean catalogSeeded = brandCount > 0;

        if (rolesSeeded && catalogSeeded) {
            return Health.up()
                    .withDetail("roles", "seeded (%d)".formatted(roleCount))
                    .withDetail("catalog", "seeded (%d)".formatted(brandCount))
                    .build();
        }
        return Health.down()
                .withDetail("roles", rolesSeeded ? "seeded" : "pending")
                .withDetail("catalog", catalogSeeded ? "seeded" : "pending")
                .build();
    }
}
