package com.atg.autonexo.backend.payment.domain.model.queries;

/**
 * Query to get all payments for a workshop.
 */
public record GetWorkshopPaymentsQuery(
    Long workshopId,
    Integer page,
    Integer size
) {
    public GetWorkshopPaymentsQuery {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID must be valid");
        }
    }
}

