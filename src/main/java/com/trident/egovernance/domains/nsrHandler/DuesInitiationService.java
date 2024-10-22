package com.trident.egovernance.domains.nsrHandler;

import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.helpers.SharedStateAmongDueInitiationAndNSRService;

import java.util.concurrent.CompletableFuture;

public interface DuesInitiationService {
    CompletableFuture<Boolean> initiateDuesDetails(NSR student, SharedStateAmongDueInitiationAndNSRService sharedState);
}
