package com.trident.egovernance.filters;

import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.service.UserDataFetcherFromMS;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class CustomAuthorityFilter extends OncePerRequestFilter {
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final WebClient webClient;

    public CustomAuthorityFilter(UserDataFetcherFromMS userDataFetcherFromMS) {
        this.userDataFetcherFromMS = userDataFetcherFromMS;
        this.webClient = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0/users").build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth && authentication.isAuthenticated()){
            Jwt jwt = jwtAuth.getToken();
            String apptoken = userDataFetcherFromMS.getAppBearerToken();
            Jwt jwts = (Jwt)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            Map<String,Object> claims = jwts.getClaims();
            String username = (String) claims.get("preferred_username");
            String uri = UriComponentsBuilder.fromPath("/"+username)
                    .queryParam("$select","displayName,jobTitle,department")
                    .toUriString();
            UserJobInformationDto userJobInformationDto = webClient.get()
                    .uri(uri)
                    .header("Authorization","Bearer "+apptoken)
                    .retrieve()
                    .bodyToMono(UserJobInformationDto.class)
                    .block();
            Collection<GrantedAuthority> newAuthorities = List.of(
                    new SimpleGrantedAuthority(userJobInformationDto.getJobTitle()),
                    new SimpleGrantedAuthority(userJobInformationDto.getDepartment())
            );
            JwtAuthenticationToken newAuth = new JwtAuthenticationToken(jwt,newAuthorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        filterChain.doFilter(request,response);
    }
}
