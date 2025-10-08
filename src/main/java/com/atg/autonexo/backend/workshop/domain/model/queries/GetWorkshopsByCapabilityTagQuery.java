package com.atg.autonexo.backend.workshop.domain.model.queries;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;

/**
 * Query to get workshops by capability tag
 */
public record GetWorkshopsByCapabilityTagQuery(CapabilityTag tag) {
    public GetWorkshopsByCapabilityTagQuery {
        if (tag == null) {
            throw new IllegalArgumentException("Capability tag cannot be null.");
        }
    }
}

