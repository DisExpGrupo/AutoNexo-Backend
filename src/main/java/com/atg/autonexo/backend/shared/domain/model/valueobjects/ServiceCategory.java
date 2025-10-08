package com.atg.autonexo.backend.shared.domain.model.valueobjects;

/**
 * Categories for service catalog classification.
 * This enum helps organize services for better UX and future matching.
 */
public enum ServiceCategory {
    MAINTENANCE("Mantenimiento", "Servicios de mantenimiento preventivo y correctivo"),
    BRAKES("Frenos", "Servicios relacionados con el sistema de frenos"),
    ENGINE("Motor", "Servicios de diagnóstico y reparación de motor"),
    TRANSMISSION("Transmisión", "Servicios de transmisión y embrague"),
    SUSPENSION("Suspensión", "Servicios de suspensión, alineación y balanceo"),
    ELECTRICAL("Eléctrico", "Servicios del sistema eléctrico del vehículo"),
    COOLING("Refrigeración", "Servicios del sistema de refrigeración"),
    EXHAUST("Escape", "Servicios del sistema de escape"),
    TIRES("Neumáticos", "Servicios relacionados con neumáticos"),
    BODYWORK("Carrocería", "Servicios de carrocería y pintura"),
    DIAGNOSTICS("Diagnóstico", "Servicios de escaneo y diagnóstico electrónico"),
    OTHER("Otros", "Otros servicios no categorizados");

    private final String displayName;
    private final String description;

    ServiceCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get ServiceCategory from string representation
     */
    public static ServiceCategory fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("ServiceCategory value cannot be null");
        }
        try {
            return ServiceCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ServiceCategory: " + value + 
                ". Valid values are: " + String.join(", ", getValidValues()));
        }
    }

    /**
     * Get all valid category names
     */
    public static String[] getValidValues() {
        ServiceCategory[] categories = ServiceCategory.values();
        String[] names = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            names[i] = categories[i].name();
        }
        return names;
    }
}

