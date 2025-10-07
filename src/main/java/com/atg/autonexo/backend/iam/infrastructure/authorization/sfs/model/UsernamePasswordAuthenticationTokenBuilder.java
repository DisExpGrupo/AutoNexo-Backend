package com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class is used to build the UsernamePasswordAuthenticationToken object
 * that is used to authenticate the user with multitenancy support.
 */
public class UsernamePasswordAuthenticationTokenBuilder {

    /**
     * This method is responsible for building the UsernamePasswordAuthenticationToken object.
     * @param principal The user details.
     * @param request The HTTP request.
     * @return The UsernamePasswordAuthenticationToken object.
     * @see UsernamePasswordAuthenticationToken
     * @see UserDetails
     */
    public static UsernamePasswordAuthenticationToken build(UserDetails principal, HttpServletRequest request) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                principal, 
                null, 
                principal.getAuthorities()
        );
        usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        return usernamePasswordAuthenticationToken;
    }

    /**
     * This method is responsible for building the UsernamePasswordAuthenticationToken object
     * without HTTP request details (useful for programmatic authentication).
     * @param principal The user details.
     * @return The UsernamePasswordAuthenticationToken object.
     */
    public static UsernamePasswordAuthenticationToken build(UserDetails principal) {
        return new UsernamePasswordAuthenticationToken(
                principal, 
                null, 
                principal.getAuthorities()
        );
    }
}
