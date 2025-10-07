package com.atg.autonexo.backend.shared.infrastructure.multitenancy;

import org.slf4j.Logger; // Usamos el Value Object de Dominio
import org.slf4j.LoggerFactory;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Thread-safe context holder for workshop information (multitenancy).
 * This class provides a way to store and retrieve the WorkshopId 
 * for the current thread/request context across all bounded contexts.
 */
public class WorkshopContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkshopContext.class);

    private static final ThreadLocal<WorkshopId> CURRENT_WORKSHOP_ID = new ThreadLocal<>();
    

    private WorkshopContext() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Set the workshop ID for the current thread.
     * @param workshopId The ID of the workshop (null if user is CAR_OWNER).
     */
    public static void setCurrentWorkshopId(Long workshopId) {
        if (workshopId != null && workshopId > 0) {
            CURRENT_WORKSHOP_ID.set(new WorkshopId(workshopId)); 
            LOGGER.debug("WorkshopContext set to ID: {}", workshopId);
        } else {
            clear();
        }
    }
    
    /**
     * Get the current WorkshopId Value Object.
     * @return The WorkshopId object, or null if no context is set.
     */
    public static WorkshopId getCurrentWorkshopId() {
        return CURRENT_WORKSHOP_ID.get();
    }
    
    /**
     * Get the current workshop ID as a primitive Long.
     * Throws IllegalStateException if the context is not set (null).
     * @return current workshop ID.
     */
    public static Long getCurrentWorkshopIdAsLong() {
        WorkshopId context = CURRENT_WORKSHOP_ID.get();
        if (context == null) {
            throw new IllegalStateException("Workshop context not found. Workshop ID must be set for WORKSHOP_WORKER access.");
        }
        return context.id();
    }
    
    /**
     * Clear the workshop context for current thread.
     * Should be called at the end of request processing (in the filter's finally block).
     */
    public static void clear() {
        CURRENT_WORKSHOP_ID.remove();
        LOGGER.debug("WorkshopContext cleared.");
    }
    
    /**
     * Check if a workshop context is currently set.
     * @return true if WorkshopId is set, false otherwise.
     */
    public static boolean hasWorkshopContext() {
        return CURRENT_WORKSHOP_ID.get() != null;
    }
}
