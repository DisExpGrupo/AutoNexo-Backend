package com.atg.autonexo.backend.matching.application.internal.queryservices;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.domain.model.queries.GetOffersByServiceRequestQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetUserOffersQuery;
import com.atg.autonexo.backend.matching.domain.model.queries.GetWorkshopOffersQuery;
import com.atg.autonexo.backend.matching.domain.services.OfferQueryService;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.OfferRepository;

/**
 * Implementation of OfferQueryService.
 */
@Service
@Transactional(readOnly = true)
public class OfferQueryServiceImpl implements OfferQueryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferQueryServiceImpl.class);
    
    private final OfferRepository offerRepository;
    
    public OfferQueryServiceImpl(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }
    
    @Override
    public List<Offer> handle(GetOffersByServiceRequestQuery query) {
        return offerRepository.findByServiceRequestId(query.serviceRequestId());
    }
    
    @Override
    public List<Offer> handle(GetWorkshopOffersQuery query) {
        if (query.status() != null) {
            return offerRepository.findByWorkshopIdAndStatus(query.workshopId().id(), query.status());
        }
        return offerRepository.findByWorkshopId(query.workshopId().id());
    }
    
    @Override
    public List<Offer> handle(GetUserOffersQuery query) {
        if (query.status() != null) {
            return offerRepository.findByUserServiceRequestsAndStatus(query.userId(), query.status());
        }
        return offerRepository.findByUserServiceRequests(query.userId());
    }
}

