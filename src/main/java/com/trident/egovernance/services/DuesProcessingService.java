package com.trident.egovernance.services;

import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.helpers.SharedStateAmongDueInitiationAndNSRService;

import java.util.concurrent.CompletableFuture;

public interface DuesProcessingService {
    CompletableFuture<Boolean> initiateDuesDetails(NSR student, SharedStateAmongDueInitiationAndNSRService sharedState);
}
