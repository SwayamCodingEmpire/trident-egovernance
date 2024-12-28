package com.trident.egovernance.domains.nsrHandler.services;

import java.util.concurrent.CompletableFuture;

public interface UserCreationService {
    String createUser(String displayName, String jobTitle, String department, String employeeId, String password, String email, long yop);
    CompletableFuture<Void> setProfilePicture(String regdNo, String userId);
}
