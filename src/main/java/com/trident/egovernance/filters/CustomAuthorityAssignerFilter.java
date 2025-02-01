package com.trident.egovernance.filters;

import com.trident.egovernance.dto.BasicMSUserDto;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.global.services.AppBearerTokenService;
import com.trident.egovernance.global.services.UserDataFetcherFromMS;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    private final Logger logger = LoggerFactory.getLogger(CustomAuthorityAssignerFilter.class);
    private final AppBearerTokenService appBearerTokenService;
    private final UserDataFetcherFromMS userDataFetcherFromMS;

    public CustomAuthorityAssignerFilter(AppBearerTokenService appBearerTokenService, UserDataFetcherFromMS userDataFetcherFromMS) {
        this.appBearerTokenService = appBearerTokenService;
        this.userDataFetcherFromMS = userDataFetcherFromMS;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth && authentication.isAuthenticated()) {
                String authorizationHeader = request.getHeader("Authorization");
                String jwtToken = authorizationHeader.substring(7);
                Jwt jwt = jwtAuth.getToken();
                String appToken = appBearerTokenService.getAppBearerToken("defaultKey");
                Jwt jwts = (Jwt) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                Map<String, Object> claims = jwts.getClaims();
                BasicMSUserDto basicMSUserDto = new BasicMSUserDto(appToken, claims.get("preferred_username").toString(), claims.get("oid").toString());
                UserJobInformationDto userJobInformationDto = userDataFetcherFromMS.fetchUserJobInformation(basicMSUserDto);
                logger.info(userJobInformationDto.toString());
                String roleJobTitle = "ROLE_" + userJobInformationDto.jobTitle().toUpperCase().replace(" ", "_");
                Collection<GrantedAuthority> newAuthorities = List.of(
                        new SimpleGrantedAuthority(roleJobTitle),
                        new SimpleGrantedAuthority(userJobInformationDto.department()),
                        new SimpleGrantedAuthority(userJobInformationDto.employeeId()),
                        new SimpleGrantedAuthority(claims.get("name").toString()),
                        new SimpleGrantedAuthority(claims.get("oid").toString()),
                        new SimpleGrantedAuthority(jwtToken)
                );
                JwtAuthenticationToken newAuth = new JwtAuthenticationToken(jwt, newAuthorities);
                SecurityContextHolder.getContext().setAuthentication(newAuth);
                logger.info("The principal is {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
        }
    }
}
