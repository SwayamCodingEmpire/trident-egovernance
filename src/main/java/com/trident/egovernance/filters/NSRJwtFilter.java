package com.trident.egovernance.filters;

import com.trident.egovernance.config.security.CustomAuthenticationToken;
import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.global.services.CustomJwtServiceImpl;
import com.trident.egovernance.global.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class NSRJwtFilter extends OncePerRequestFilter {
    private final CustomJwtServiceImpl customJwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public NSRJwtFilter(CustomJwtServiceImpl customJwtService, CustomUserDetailsService customUserDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.customJwtService = customJwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("NSR-Authorization");
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            logger.info("NSR Authorization header is not present");
            filterChain.doFilter(request,response);
            return;
        }
        try{
            logger.info("NSR filter working");
            final String jwtToken = authHeader.substring(7);
            final String username = customJwtService.extractUsername(jwtToken);
            if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                CustomUserDetails userDetails = (CustomUserDetails) this.customUserDetailsService.loadUserByUsername(username);
                logger.info(userDetails.getUsername());
                if(customJwtService.isTokenValid(jwtToken,userDetails)){
                    logger.info("Username is not null and authentication is null and its valid");
                    CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken(userDetails.getAuthorities(),userDetails,null);
                    logger.info(authenticationToken.toString());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info(authenticationToken.toString());
                    logger.info("Authentication successful for user");
                    authenticationToken.setAuthenticated(true);
                    logger.info(authenticationToken.toString());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
//            StringBuilder json = new StringBuilder();
//            BufferedReader reader = request.getReader();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                json.append(line);
//            }
//            String rawJson = json.toString();
//            logger.info("The Json is " + rawJson);
            filterChain.doFilter(request,response);
        }catch (Exception e){
            logger.info("NSR filter exception");
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
