package com.trident.egovernance.global.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    private final AppBearerTokenService appBearerTokenService;

    public SchedulerService(AppBearerTokenService appBearerTokenService) {
        this.appBearerTokenService = appBearerTokenService;
    }

    @Scheduled(fixedRate = 3600000)
    public void scheduleAppBearerTokenFetching(){
        appBearerTokenService.getAppBearerTokenForScheduler("defaultKey");
    }
}
