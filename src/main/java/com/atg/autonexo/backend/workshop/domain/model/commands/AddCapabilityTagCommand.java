package com.atg.autonexo.backend.workshop.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;

/**
 * Command to add a capability tag to a workshop
 */
public record AddCapabilityTagCommand(
    Long workshopId,
    CapabilityTag tag
) {
    public AddCapabilityTagCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        if (tag == null) {
            throw new IllegalArgumentException("Capability tag cannot be null.");
        }
    }
}

