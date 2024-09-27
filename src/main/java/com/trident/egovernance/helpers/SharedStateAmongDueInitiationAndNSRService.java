package com.trident.egovernance.helpers;

public class SharedStateAmongDueInitiationAndNSRService {
    private volatile boolean proceed = true;

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }
}
