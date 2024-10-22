package com.trident.egovernance.global.helpers;

public class SharedStateAmongDueInitiationAndNSRService {
    private volatile boolean proceed = true;

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }
}
