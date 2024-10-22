package com.trident.egovernance.global.services;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.repositories.redisRepositories.NSRRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final NSRRepository nsrRepository;

    public CustomUserDetailsService(NSRRepository nsrRepository) {
        this.nsrRepository = nsrRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NSR nsr =  nsrRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new CustomUserDetails(nsr);
    }
}
