package com.trident.egovernance.services;

import com.trident.egovernance.dtos.Login;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    UserDetails authenticate(Login login);
}
