package com.trident.egovernance.exceptions;

public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException(String message) {
        super(message);
    }
}
