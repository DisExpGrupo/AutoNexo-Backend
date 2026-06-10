package com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.pipeline;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.atg.autonexo.backend.shared.infrastructure.web.ErrorCode;
import com.atg.autonexo.backend.shared.infrastructure.web.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unauthorized Request Handler Entry Point
 * <p>
 * Handles unauthenticated requests rejected by Spring Security and
 * returns the standardized {@link ErrorResponse} JSON payload so the
 * frontend can parse it consistently with all other error responses.
 * </p>
 */
@Component
public class UnauthorizedRequestHandlerEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedRequestHandlerEntryPoint.class);
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {

        LOGGER.warn("Unauthorized request to {}: {}",
                request.getServletPath(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse body = ErrorResponse.of(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                ErrorCode.UNAUTHORIZED,
                "Authentication required",
                request.getServletPath()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}
