package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.FeeCollectionRepository;
import org.springframework.stereotype.Service;

@Service
public class HostelRegistrationService {
    private final FeeCollectionRepository feeCollectionRepository;

    public HostelRegistrationService(FeeCollectionRepository feeCollectionRepository) {
        this.feeCollectionRepository = feeCollectionRepository;
    }
}
