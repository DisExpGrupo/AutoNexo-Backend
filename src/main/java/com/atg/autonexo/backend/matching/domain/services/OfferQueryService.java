package com.atg.autonexo.backend.matching.domain.services;

import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.domain.model.queries.GetOffersByServiceRequestQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetUserOffersQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopOffersQuery;

import java.util.List;

/**
 * Domain service interface for Offer query operations.
 */
public interface OfferQueryService {
    
    List<Offer> handle(GetOffersByServiceRequestQuery query);
    
    List<Offer> handle(GetWorkshopOffersQuery query);
    
    List<Offer> handle(GetUserOffersQuery query);
}

