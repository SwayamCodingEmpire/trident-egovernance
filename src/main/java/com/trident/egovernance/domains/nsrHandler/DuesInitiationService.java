package com.trident.egovernance.domains.nsrHandler;

import com.trident.egovernance.dto.DuesDetailsInitiationDTO;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.helpers.SharedStateAmongDueInitiationAndNSRService;
import org.springframework.transaction.TransactionStatus;

import java.util.concurrent.CompletableFuture;

public interface DuesInitiationService {
    Boolean initiateDuesDetails(DuesDetailsInitiationDTO student, SharedStateAmongDueInitiationAndNSRService sharedState);
    CompletableFuture<Boolean> initiateDues(DuesDetailsInitiationDTO student, SharedStateAmongDueInitiationAndNSRService sharedState);
}
