package com.trident.egovernance.filters;

import com.trident.egovernance.dto.BasicMSUserDto;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.global.services.AppBearerTokenService;
import com.trident.egovernance.global.services.UserDataFetcherFromMS;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class CustomAuthorityAssignerFilter extends OncePerRequestFilter {
    private final AppBearerTokenService appBearerTokenService;
    private final UserDataFetcherFromMS userDataFetcherFromMS;

    public CustomAuthorityAssignerFilter(AppBearerTokenService appBearerTokenService, UserDataFetcherFromMS userDataFetcherFromMS) {
        this.appBearerTokenService = appBearerTokenService;
        this.userDataFetcherFromMS = userDataFetcherFromMS;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth && authentication.isAuthenticated()){
            Jwt jwt = jwtAuth.getToken();
            String apptoken = appBearerTokenService.getAppBearerToken("defaultKey");
            Jwt jwts = (Jwt)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            Map<String,Object> claims = jwts.getClaims();
            BasicMSUserDto basicMSUserDto = new BasicMSUserDto(apptoken,claims.get("preferred_username").toString());
            UserJobInformationDto userJobInformationDto = userDataFetcherFromMS.fetchUserJobInformation(basicMSUserDto);
            String roleJobTitle = "ROLE_" + userJobInformationDto.jobTitle().toUpperCase().replace(" ", "_");
            Collection<GrantedAuthority> newAuthorities = List.of(
                    new SimpleGrantedAuthority(roleJobTitle),
                    new SimpleGrantedAuthority(userJobInformationDto.department()),
                    new SimpleGrantedAuthority(userJobInformationDto.employeeId()),
                    new SimpleGrantedAuthority(claims.get("name").toString())
            );
            JwtAuthenticationToken newAuth = new JwtAuthenticationToken(jwt,newAuthorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        filterChain.doFilter(request,response);
    }
}
