package com.trident.egovernance.services;

import com.trident.egovernance.dto.Login;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    UserDetails authenticate(Login login);
}
