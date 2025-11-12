package com.atg.autonexo.backend.payment.domain.model.queries;

import java.time.LocalDate;

/**
 * Query to get upcoming subscription renewals.
 */
public record GetUpcomingRenewalsQuery(
    LocalDate from,
    LocalDate to
) {
    public GetUpcomingRenewalsQuery {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and to dates cannot be null");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("To date must be after from date");
        }
    }
}

