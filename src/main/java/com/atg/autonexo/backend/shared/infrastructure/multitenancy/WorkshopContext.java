package com.atg.autonexo.backend.shared.infrastructure.multitenancy;

/**
 * Thread-safe context holder for tenant information
 * This class provides a way to store and retrieve tenant information
 * for the current thread/request context across all bounded contexts
 */
public class WorkshopContext {
    
    private static final ThreadLocal<Long> CURRENT_WORKSHOP_ID = new ThreadLocal<>();
    private static final Long DEFAULT_WORKSHOP_ID = 1L;
    
    private WorkshopContext() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Set the workshop ID for current thread
     * @param workshopId the workshop ID
     */
    public static void setCurrentTenantId(Long workshopId) {
        CURRENT_WORKSHOP_ID.set(workshopId != null ? workshopId : DEFAULT_WORKSHOP_ID);
    }
    
    /**
     * Get the current workshop ID
     * @return current tenant ID or default if not set
     */
    public static Long getCurrentTenantId() {
        if (CURRENT_WORKSHOP_ID.get() == null) {
            throw new IllegalStateException("Workshop context not found. Workshop ID must be set before accessing it.");
        }
        return CURRENT_WORKSHOP_ID.get();
    }
    
    /**
     * Clear the tenant context for current thread
     * Should be called at the end of request processing
     */
    public static void clear() {
        CURRENT_WORKSHOP_ID.remove();
    }
    
    /**
     * Check if a tenant is currently set
     * @return true if tenant is set, false otherwise
     */
    public static boolean hasTenant() {
        return CURRENT_WORKSHOP_ID.get() != null;
    }
} 