package com.trident.egovernance.domains.nsrHandler.services;

public interface UserCreationService {
    String createUser(String displayName, String jobTitle, String department, String employeeId, String password, String email, int yop);
}
