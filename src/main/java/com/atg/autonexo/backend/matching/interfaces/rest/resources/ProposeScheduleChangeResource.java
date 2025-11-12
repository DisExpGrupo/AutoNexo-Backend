package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/**
 * Resource for proposing schedule change.
 */
public record ProposeScheduleChangeResource(
    @NotNull(message = "New scheduled date is required")
    LocalDateTime newScheduledDate
) {}

