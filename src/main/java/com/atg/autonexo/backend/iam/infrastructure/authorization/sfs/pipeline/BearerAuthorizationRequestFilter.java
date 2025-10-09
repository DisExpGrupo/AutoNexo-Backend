package com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.pipeline;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsServiceExtension;
import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.atg.autonexo.backend.iam.infrastructure.tokens.jwt.BearerTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Bearer Authorization Request Filter
 * <p>
 * This filter is responsible for processing the JWT token from the request header
 * and setting the authentication in the security context if the token is valid.
 * </p>
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);

    private final BearerTokenService tokenService;
    private final UserDetailsServiceExtension userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsServiceExtension userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = tokenService.getBearerTokenFrom(request);
            
            if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
                Long userId = tokenService.getUserIdFromToken(token);
                
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                LOGGER.info("User loaded: {}", userDetails);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                
                var authenticationToken = UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request);
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
                
                LOGGER.debug("Authentication set for user: {}", userId);
            }
        } catch (Exception exception) {
            LOGGER.error("Cannot set user authentication: {}", exception.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
} 