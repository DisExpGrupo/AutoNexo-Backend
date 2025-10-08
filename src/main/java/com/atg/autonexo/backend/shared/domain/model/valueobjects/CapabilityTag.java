package com.atg.autonexo.backend.shared.domain.model.valueobjects;

/**
 * Tags that define workshop capabilities and specializations.
 * Used for filtering, searching, and future matching algorithms.
 */
public enum CapabilityTag {
    
    // === VEHICLE TYPES ===
    LIGHT_VEHICLES("Vehículos livianos", TagCategory.VEHICLE_TYPE),
    HEAVY_VEHICLES("Vehículos pesados", TagCategory.VEHICLE_TYPE),
    MOTORCYCLES("Motocicletas", TagCategory.VEHICLE_TYPE),
    MOTORCYCLES_ONLY("Solo motocicletas", TagCategory.VEHICLE_TYPE),
    TRUCKS("Camiones", TagCategory.VEHICLE_TYPE),
    BUSES("Autobuses", TagCategory.VEHICLE_TYPE),
    SUVS("SUVs y camionetas", TagCategory.VEHICLE_TYPE),
    ELECTRIC_VEHICLES("Vehículos eléctricos", TagCategory.VEHICLE_TYPE),
    HYBRID_VEHICLES("Vehículos híbridos", TagCategory.VEHICLE_TYPE),
    
    // === BRANDS (Popular ones) ===
    TOYOTA("Toyota", TagCategory.BRAND),
    HONDA("Honda", TagCategory.BRAND),
    NISSAN("Nissan", TagCategory.BRAND),
    MAZDA("Mazda", TagCategory.BRAND),
    HYUNDAI("Hyundai", TagCategory.BRAND),
    KIA("Kia", TagCategory.BRAND),
    CHEVROLET("Chevrolet", TagCategory.BRAND),
    FORD("Ford", TagCategory.BRAND),
    VOLKSWAGEN("Volkswagen", TagCategory.BRAND),
    BMW("BMW", TagCategory.BRAND),
    MERCEDES_BENZ("Mercedes-Benz", TagCategory.BRAND),
    AUDI("Audi", TagCategory.BRAND),
    SUZUKI("Suzuki", TagCategory.BRAND),
    MITSUBISHI("Mitsubishi", TagCategory.BRAND),
    SUBARU("Subaru", TagCategory.BRAND),
    JEEP("Jeep", TagCategory.BRAND),
    
    // === SPECIALIZATIONS ===
    DIESEL_SPECIALIST("Especialista en diesel", TagCategory.SPECIALIZATION),
    TRANSMISSION_SPECIALIST("Especialista en transmisiones", TagCategory.SPECIALIZATION),
    ENGINE_SPECIALIST("Especialista en motores", TagCategory.SPECIALIZATION),
    ELECTRICAL_SPECIALIST("Especialista en electricidad", TagCategory.SPECIALIZATION),
    AC_SPECIALIST("Especialista en A/C", TagCategory.SPECIALIZATION),
    BODYWORK_SPECIALIST("Especialista en carrocería", TagCategory.SPECIALIZATION),
    PAINT_SPECIALIST("Especialista en pintura", TagCategory.SPECIALIZATION),
    SUSPENSION_SPECIALIST("Especialista en suspensión", TagCategory.SPECIALIZATION),
    BRAKE_SPECIALIST("Especialista en frenos", TagCategory.SPECIALIZATION),
    DIAGNOSTICS_SPECIALIST("Especialista en diagnóstico", TagCategory.SPECIALIZATION),
    
    // === SERVICES ===
    TOW_SERVICE("Servicio de grúa", TagCategory.SERVICE),
    HOME_SERVICE("Servicio a domicilio", TagCategory.SERVICE),
    EMERGENCY_SERVICE("Servicio de emergencia 24/7", TagCategory.SERVICE),
    WARRANTY_SERVICE("Servicio con garantía", TagCategory.SERVICE),
    INSURANCE_APPROVED("Aprobado por aseguradoras", TagCategory.SERVICE),
    CERTIFIED_MECHANICS("Mecánicos certificados", TagCategory.SERVICE),
    ORIGINAL_PARTS("Repuestos originales", TagCategory.SERVICE),
    AFTERMARKET_PARTS("Repuestos alternativos", TagCategory.SERVICE),
    
    // === FACILITIES ===
    WAITING_AREA("Área de espera", TagCategory.FACILITY),
    WIFI("WiFi gratis", TagCategory.FACILITY),
    PARKING("Estacionamiento", TagCategory.FACILITY),
    COURTESY_CAR("Auto de cortesía", TagCategory.FACILITY),
    DIAGNOSTIC_EQUIPMENT("Equipo de diagnóstico avanzado", TagCategory.FACILITY),
    LIFT_EQUIPMENT("Equipo de elevación", TagCategory.FACILITY);

    private final String displayName;
    private final TagCategory category;

    CapabilityTag(String displayName, TagCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TagCategory getCategory() {
        return category;
    }

    /**
     * Get CapabilityTag from string representation
     */
    public static CapabilityTag fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("CapabilityTag value cannot be null");
        }
        try {
            return CapabilityTag.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CapabilityTag: " + value);
        }
    }

    /**
     * Get all tags for a specific category
     */
    public static CapabilityTag[] getByCategory(TagCategory category) {
        if (category == null) {
            return new CapabilityTag[0];
        }
        return java.util.Arrays.stream(CapabilityTag.values())
                .filter(tag -> tag.getCategory() == category)
                .toArray(CapabilityTag[]::new);
    }

    /**
     * Get all valid tag names
     */
    public static String[] getValidValues() {
        CapabilityTag[] tags = CapabilityTag.values();
        String[] names = new String[tags.length];
        for (int i = 0; i < tags.length; i++) {
            names[i] = tags[i].name();
        }
        return names;
    }

    /**
     * Category enum for organizing capability tags
     */
    public enum TagCategory {
        VEHICLE_TYPE("Tipos de vehículos"),
        BRAND("Marcas especializadas"),
        SPECIALIZATION("Especializaciones técnicas"),
        SERVICE("Servicios adicionales"),
        FACILITY("Instalaciones y comodidades");

        private final String displayName;

        TagCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

