package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

/**
 * Resource for cancelling a service booking.
 */
public record CancelServiceBookingResource(
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    String cancellationReason
) {}

