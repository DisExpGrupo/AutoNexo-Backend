package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to add a new staff member to a workshop
 */
public record AddStaffMemberCommand(
    Long workshopId,
    Long userId,
    Long primaryLocationId
) {
    public AddStaffMemberCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative.");
        }
    }
}

