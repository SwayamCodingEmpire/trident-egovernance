package com.trident.egovernance.config.security;

import com.trident.egovernance.filters.CustomAuthorityAssignerFilter;
import com.trident.egovernance.filters.NSRJwtFilter;
import com.trident.egovernance.global.helpers.AppConstants;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    private URL jwkSetUrl;

    private final CustomAuthorityAssignerFilter customAuthorityAssignerFilter;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final NSRJwtFilter nsrJwtFilter;

    public SecurityConfig(CustomAuthorityAssignerFilter customAuthorityAssignerFilter, CustomAuthenticationProvider customAuthenticationProvider, NSRJwtFilter nsrJwtFilter) {
        this.customAuthorityAssignerFilter = customAuthorityAssignerFilter;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.nsrJwtFilter = nsrJwtFilter;
    }
//    private final OAuthenticationSuccessHandler handler;

//    public SecurityConfig(OAuthenticationSuccessHandler handler) {
//        this.handler = handler;
//    }

    private static final String[] PUBLIC_URLS = {
            "/test/**",
            "/menu/**",
            "/NSR/**",
            "/public/**",
            "/server1/**",
            "/v3/api-docs",
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/payment/**",
            "/public/**",
            "/accounts-section/**",
            "/initiate-session/**",
            "/office/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/student-portal/**").hasRole("STUDENT");
            authorize.requestMatchers(PUBLIC_URLS).permitAll();
            authorize.requestMatchers("/test/hello").hasRole("NSR");
            authorize.requestMatchers("/api/**").authenticated();
        }).sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        httpSecurity.authenticationProvider(customAuthenticationProvider);
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
//        httpSecurity.oauth2Login(oauth -> {
//            oauth.successHandler(handler);
//        });
//        httpSecurity.oauth2Login(Customizer.withDefaults());
        httpSecurity.oauth2ResourceServer((oauth2ResourceServer) ->
                oauth2ResourceServer
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                        )
        );
        httpSecurity.addFilterAfter(customAuthorityAssignerFilter, BearerTokenAuthenticationFilter.class);
        httpSecurity.addFilterAfter(nsrJwtFilter, CustomAuthorityAssignerFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://127.0.0.1:49865","http://127.0.0.1:50296"));

        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

//    @Bean
//    public JwtDecoder jwtDecoder(){
//        try{
//            this.jwkSetUrl = new URL(this.jwkSetUri);
//            JWSKeySelector<SecurityContext> jwsKeySelector =
//                    JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(this.jwkSetUrl);
//
//            DefaultJWTProcessor<SecurityContext> jwtProcessor =
//                    new DefaultJWTProcessor<>();
//            jwtProcessor.setJWSKeySelector(jwsKeySelector);
//
//            return new NimbusJwtDecoder(jwtProcessor);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new RuntimeException("Error in creating the bean");
//        }

    @Bean
    public JwtDecoder jwtDecoder() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5))  // Set response timeout
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);  // Set connection timeout

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        // Implement the retry mechanism
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .restOperations(new RestTemplateWithRetry(webClient, 3)) // Retry 3 times
                .build();
    }

    private static class RestTemplateWithRetry extends RestTemplate {
        private final WebClient webClient;
        private final int maxRetries;

        public RestTemplateWithRetry(WebClient webClient, int maxRetries) {
            this.webClient = webClient;
            this.maxRetries = maxRetries;
        }

        @Override
        public <T> T getForObject(URI url, Class<T> responseType) {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(responseType)
                    .retryWhen(Retry.fixedDelay(maxRetries, Duration.ofSeconds(3))) // Retry mechanism
                    .block(); // Convert Mono to blocking call
        }
    }
}
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        try {
//            this.jwkSetUrl = new URL(this.jwkSetUri);
//            HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(10)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
//            WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
//            JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(jwkSetUrl, (JWKSource) webClient);
//            JWSKeySelector<SecurityContext> jwsKeySelector =
//                    JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(this.jwkSetUrl);
//
//            DefaultJWTProcessor<SecurityContext> jwtProcessor =
//                    new DefaultJWTProcessor<>();
//            jwtProcessor.setJWSKeySelector(jwsKeySelector);
//
//            return new NimbusJwtDecoder(jwtProcessor);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error in creating the bean");
//
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new RuntimeException("Error in creating the bean");
//        }
//    }

