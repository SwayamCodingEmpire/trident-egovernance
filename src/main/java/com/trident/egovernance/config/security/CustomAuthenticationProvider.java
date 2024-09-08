package com.trident.egovernance.config.security;

import com.trident.egovernance.dtos.Login;
import com.trident.egovernance.entities.redisEntities.NSR;
import com.trident.egovernance.repositories.redisRepositories.NSRRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
@Configuration
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final NSRRepository nsrRepository;

    public CustomAuthenticationProvider(NSRRepository nsrRepository) {
        this.nsrRepository = nsrRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof CustomAuthenticationToken)){
            logger.info("Invalid Authentication Token");
            return null;
        }
        CustomAuthenticationToken token = (CustomAuthenticationToken) authentication;
        String applicationNo = (String) authentication.getPrincipal();
        logger.info("Authenticating user with application no. "+applicationNo);
        logger.info("Credentials : "+token.getCredentials().getClass().getName());
        Long rank = (Long) token.getCredentials();
        logger.info("Authenticating user with rank "+rank);
        NSR nsr = nsrRepository.findById(applicationNo).orElseThrow(()->new BadCredentialsException("User not found"));
        logger.info("User found : {}",nsr.toString());
        if(!nsr.getRank().equals(rank)){
            logger.info(nsr.getRank() + "" + rank);
            logger.error("Invalid Rank");
            throw new BadCredentialsException("Invalid Rank");
        }
        return new CustomAuthenticationToken(
                Collections.singletonList(new SimpleGrantedAuthority("NSR")),
                new CustomUserDetails(nsr),
                new Login(applicationNo,rank)
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
