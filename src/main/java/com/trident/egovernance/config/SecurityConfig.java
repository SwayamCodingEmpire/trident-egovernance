package com.trident.egovernance.config;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.net.URL;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    private URL jwkSetUrl;
//    private final OAuthenticationSuccessHandler handler;

//    public SecurityConfig(OAuthenticationSuccessHandler handler) {
//        this.handler = handler;
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize ->{
            authorize.requestMatchers("/user/**").permitAll();
            authorize.requestMatchers("/api/**").authenticated();
            authorize.anyRequest().permitAll();
                });
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
//        httpSecurity.oauth2Login(oauth -> {
//            oauth.successHandler(handler);
//        });
        httpSecurity.oauth2Login(Customizer.withDefaults());
        httpSecurity.oauth2ResourceServer((oauth2ResourceServer) ->
                oauth2ResourceServer
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                        )
        );
        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://127.0.0.1:49865","http://127.0.0.1:50296"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return new CorsFilter(source);
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        try{
            this.jwkSetUrl = new URL(this.jwkSetUri);
            JWSKeySelector<SecurityContext> jwsKeySelector =
                    JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(this.jwkSetUrl);

            DefaultJWTProcessor<SecurityContext> jwtProcessor =
                    new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);

            return new NimbusJwtDecoder(jwtProcessor);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error in creating the bean");
        }

    }

}
