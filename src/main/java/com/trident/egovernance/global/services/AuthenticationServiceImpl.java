package com.trident.egovernance.global.services;

import com.trident.egovernance.config.security.CustomAuthenticationProvider;
import com.trident.egovernance.config.security.CustomAuthenticationToken;
import com.trident.egovernance.dto.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final CustomAuthenticationProvider customAuthenticationProvider;
    public AuthenticationServiceImpl(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Override
    public UserDetails authenticate(Login login) {
        try {
            logger.info("Authenticating user with application no. "+login.applicationNo()+" and rank "+login.rank());
            Authentication authentication = customAuthenticationProvider.authenticate(new CustomAuthenticationToken(null,login.applicationNo(),login.rank()));
            if(authentication==null){
                logger.error("User not found");
                throw new UsernameNotFoundException("User not found");
            }
            logger.info(authentication.toString());
            logger.info("User authenticated successfully");
            return (UserDetails) authentication.getPrincipal();
        }catch (Exception e){
            logger.error(String.valueOf(e));
            throw new BadCredentialsException("Invalid username or password");

        }

    }
}
