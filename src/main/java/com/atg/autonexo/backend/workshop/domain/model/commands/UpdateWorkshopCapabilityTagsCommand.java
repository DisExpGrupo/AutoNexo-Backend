package com.atg.autonexo.backend.workshop.domain.model.commands;

import java.util.Set;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;

/**
 * Command to update workshop capability tags (replaces all existing tags)
 */
public record UpdateWorkshopCapabilityTagsCommand(
    Long workshopId,
    Set<CapabilityTag> tags
) {
    public UpdateWorkshopCapabilityTagsCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        // tags can be null or empty to clear all tags
    }
}

