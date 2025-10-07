package com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsServiceExtension;
import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter; // NUEVA IMPORTACIÓN
import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.pipeline.WorkshopExtractionFilter;
import com.atg.autonexo.backend.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.atg.autonexo.backend.iam.infrastructure.tokens.jwt.BearerTokenService;

/**
 * Web Security Configuration.
 * <p>
 * This class is responsible for configuring the web security.
 * It enables the method security and configures the security filter chain.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final UserDetailsServiceExtension userDetailsService;
    private final BearerTokenService tokenService;
    private final BCryptHashingService hashingService;
    private final AuthenticationEntryPoint unauthorizedRequestHandler;
    private final WorkshopExtractionFilter workshopExtractionFilter; // NUEVO CAMPO

    /**
     * Constructor for Dependency Injection.
     */
    public WebSecurityConfiguration(
            @Qualifier("defaultUserDetailsService") UserDetailsServiceExtension userDetailsService,
            BearerTokenService tokenService,
            BCryptHashingService hashingService,
            AuthenticationEntryPoint authenticationEntryPoint,
            WorkshopExtractionFilter workshopExtractionFilter 
    ) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.unauthorizedRequestHandler = authenticationEntryPoint;
        this.workshopExtractionFilter = workshopExtractionFilter; 
    }
    /**
     * Creates the Bearer Authorization Request Filter.
     */
    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsService); 
    }
    

    /**
     * Creates the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates the authentication provider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(hashingService);
        return authenticationProvider;
    }

    /**
     * Creates the password encoder (using the hashing service).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return hashingService;
    }

    // --- Cadena de Filtros de Seguridad ---

    /**
     * Configures the security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS configuration
        http.cors(configurer -> configurer.configurationSource(_ -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }));
        
        // Disable CSRF, configure exception handling and session policy
        http.csrf(csrfConfigurer -> csrfConfigurer.disable())
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement( customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Authorization configuration
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/v1/users/signup", // User registration
                                "/api/v1/users/signin", // User authentication
                                "/api/v1/users/available-roles", // Available roles for registration
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll() // Public routes
                        .anyRequest().authenticated()); // All other requests require authentication

        // Authentication provider configuration
        http.authenticationProvider(authenticationProvider());
        
        // Filter chain configuration
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(workshopExtractionFilter, BearerAuthorizationRequestFilter.class);

        return http.build();
    }
}