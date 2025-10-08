package com.atg.autonexo.backend.shared.domain.model.valueobjects;

/**
 * Catalog of known services in the AutoNexo system.
 * Workshops can link their custom service templates to these predefined services.
 * This helps with standardization, search, and future matching algorithms.
 */
public enum ServiceCatalog {
    
    // === MAINTENANCE ===
    OIL_CHANGE(ServiceCategory.MAINTENANCE, "Cambio de aceite", "Cambio de aceite de motor y filtro"),
    OIL_FILTER_CHANGE(ServiceCategory.MAINTENANCE, "Cambio de filtro de aceite", "Reemplazo del filtro de aceite"),
    AIR_FILTER_CHANGE(ServiceCategory.MAINTENANCE, "Cambio de filtro de aire", "Reemplazo del filtro de aire del motor"),
    FUEL_FILTER_CHANGE(ServiceCategory.MAINTENANCE, "Cambio de filtro de combustible", "Reemplazo del filtro de combustible"),
    CABIN_FILTER_CHANGE(ServiceCategory.MAINTENANCE, "Cambio de filtro de habitáculo", "Reemplazo del filtro de aire del habitáculo"),
    GENERAL_INSPECTION(ServiceCategory.MAINTENANCE, "Revisión general", "Inspección completa de todos los sistemas"),
    PREVENTIVE_MAINTENANCE(ServiceCategory.MAINTENANCE, "Mantenimiento preventivo", "Servicio de mantenimiento preventivo según kilometraje"),
    
    // === BRAKES ===
    BRAKE_PAD_REPLACEMENT(ServiceCategory.BRAKES, "Cambio de pastillas de freno", "Reemplazo de pastillas de freno delanteras o traseras"),
    BRAKE_DISC_REPLACEMENT(ServiceCategory.BRAKES, "Cambio de discos de freno", "Reemplazo de discos de freno"),
    BRAKE_FLUID_CHANGE(ServiceCategory.BRAKES, "Cambio de líquido de frenos", "Purga y reemplazo del líquido de frenos"),
    BRAKE_SYSTEM_INSPECTION(ServiceCategory.BRAKES, "Inspección de frenos", "Revisión completa del sistema de frenos"),
    
    // === ENGINE ===
    ENGINE_DIAGNOSTICS(ServiceCategory.ENGINE, "Diagnóstico de motor", "Escaneo y diagnóstico electrónico del motor"),
    SPARK_PLUG_REPLACEMENT(ServiceCategory.ENGINE, "Cambio de bujías", "Reemplazo de bujías de encendido"),
    TIMING_BELT_REPLACEMENT(ServiceCategory.ENGINE, "Cambio de correa de distribución", "Reemplazo de correa de distribución y tensores"),
    INJECTOR_CLEANING(ServiceCategory.ENGINE, "Limpieza de inyectores", "Limpieza ultrasónica de inyectores"),
    ENGINE_TUNEUP(ServiceCategory.ENGINE, "Afinamiento de motor", "Afinamiento completo del motor"),
    ENGINE_OVERHAUL(ServiceCategory.ENGINE, "Rectificación de motor", "Rectificación completa del motor"),
    
    // === TRANSMISSION ===
    TRANSMISSION_OIL_CHANGE(ServiceCategory.TRANSMISSION, "Cambio de aceite de transmisión", "Reemplazo del aceite de caja de cambios"),
    CLUTCH_REPLACEMENT(ServiceCategory.TRANSMISSION, "Cambio de embrague", "Reemplazo de kit de embrague completo"),
    TRANSMISSION_DIAGNOSTICS(ServiceCategory.TRANSMISSION, "Diagnóstico de transmisión", "Diagnóstico del sistema de transmisión"),
    
    // === SUSPENSION ===
    WHEEL_ALIGNMENT(ServiceCategory.SUSPENSION, "Alineación", "Alineación de ruedas y geometría"),
    WHEEL_BALANCING(ServiceCategory.SUSPENSION, "Balanceo", "Balanceo de ruedas"),
    SHOCK_ABSORBER_REPLACEMENT(ServiceCategory.SUSPENSION, "Cambio de amortiguadores", "Reemplazo de amortiguadores"),
    SUSPENSION_INSPECTION(ServiceCategory.SUSPENSION, "Inspección de suspensión", "Revisión completa del sistema de suspensión"),
    
    // === ELECTRICAL ===
    BATTERY_REPLACEMENT(ServiceCategory.ELECTRICAL, "Cambio de batería", "Reemplazo de batería del vehículo"),
    ALTERNATOR_REPAIR(ServiceCategory.ELECTRICAL, "Reparación de alternador", "Reparación o reemplazo del alternador"),
    STARTER_REPAIR(ServiceCategory.ELECTRICAL, "Reparación de motor de arranque", "Reparación o reemplazo del motor de arranque"),
    ELECTRICAL_DIAGNOSTICS(ServiceCategory.ELECTRICAL, "Diagnóstico eléctrico", "Diagnóstico del sistema eléctrico"),
    HEADLIGHT_RESTORATION(ServiceCategory.ELECTRICAL, "Restauración de faros", "Pulido y restauración de faros"),
    
    // === COOLING ===
    COOLANT_CHANGE(ServiceCategory.COOLING, "Cambio de refrigerante", "Reemplazo del líquido refrigerante"),
    RADIATOR_REPAIR(ServiceCategory.COOLING, "Reparación de radiador", "Reparación o reemplazo del radiador"),
    THERMOSTAT_REPLACEMENT(ServiceCategory.COOLING, "Cambio de termostato", "Reemplazo del termostato"),
    WATER_PUMP_REPLACEMENT(ServiceCategory.COOLING, "Cambio de bomba de agua", "Reemplazo de la bomba de agua"),
    
    // === EXHAUST ===
    EXHAUST_REPAIR(ServiceCategory.EXHAUST, "Reparación de escape", "Reparación del sistema de escape"),
    MUFFLER_REPLACEMENT(ServiceCategory.EXHAUST, "Cambio de silenciador", "Reemplazo del silenciador"),
    CATALYTIC_CONVERTER_REPLACEMENT(ServiceCategory.EXHAUST, "Cambio de convertidor catalítico", "Reemplazo del convertidor catalítico"),
    
    // === TIRES ===
    TIRE_CHANGE(ServiceCategory.TIRES, "Cambio de neumáticos", "Montaje y desmontaje de neumáticos"),
    TIRE_ROTATION(ServiceCategory.TIRES, "Rotación de neumáticos", "Rotación de neumáticos para desgaste uniforme"),
    TIRE_REPAIR(ServiceCategory.TIRES, "Reparación de neumático", "Parchado de neumático"),
    
    // === BODYWORK ===
    DENT_REPAIR(ServiceCategory.BODYWORK, "Reparación de abolladuras", "Eliminación de abolladuras"),
    PAINT_JOB(ServiceCategory.BODYWORK, "Pintura", "Servicio de pintura completa o parcial"),
    SCRATCH_REPAIR(ServiceCategory.BODYWORK, "Reparación de rayones", "Eliminación de rayones"),
    
    // === DIAGNOSTICS ===
    ELECTRONIC_SCAN(ServiceCategory.DIAGNOSTICS, "Escaneo electrónico", "Escaneo completo de computadora del vehículo"),
    CHECK_ENGINE_DIAGNOSTICS(ServiceCategory.DIAGNOSTICS, "Diagnóstico check engine", "Diagnóstico de luz check engine"),
    PRE_PURCHASE_INSPECTION(ServiceCategory.DIAGNOSTICS, "Inspección pre-compra", "Inspección completa antes de comprar un vehículo"),
    
    // === OTHER ===
    AC_SERVICE(ServiceCategory.OTHER, "Servicio de aire acondicionado", "Recarga y mantenimiento de A/C"),
    GLASS_REPLACEMENT(ServiceCategory.OTHER, "Cambio de vidrios", "Reemplazo de parabrisas o cristales"),
    DETAILING(ServiceCategory.OTHER, "Detailing", "Lavado y pulido profesional"),
    CUSTOM_SERVICE(ServiceCategory.OTHER, "Servicio personalizado", "Servicio no catalogado");

    private final ServiceCategory category;
    private final String displayName;
    private final String defaultDescription;

    ServiceCatalog(ServiceCategory category, String displayName, String defaultDescription) {
        this.category = category;
        this.displayName = displayName;
        this.defaultDescription = defaultDescription;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    /**
     * Get ServiceCatalog from string representation
     */
    public static ServiceCatalog fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("ServiceCatalog value cannot be null");
        }
        try {
            return ServiceCatalog.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ServiceCatalog: " + value);
        }
    }

    /**
     * Get all services for a specific category
     */
    public static ServiceCatalog[] getByCategory(ServiceCategory category) {
        if (category == null) {
            return new ServiceCatalog[0];
        }
        return java.util.Arrays.stream(ServiceCatalog.values())
                .filter(service -> service.getCategory() == category)
                .toArray(ServiceCatalog[]::new);
    }

    /**
     * Get all valid service names
     */
    public static String[] getValidValues() {
        ServiceCatalog[] services = ServiceCatalog.values();
        String[] names = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            names[i] = services[i].name();
        }
        return names;
    }
}

