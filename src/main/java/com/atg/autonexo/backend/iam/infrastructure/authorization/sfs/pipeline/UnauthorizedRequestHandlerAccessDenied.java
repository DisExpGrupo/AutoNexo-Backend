package com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.pipeline;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.atg.autonexo.backend.shared.infrastructure.web.ErrorCode;
import com.atg.autonexo.backend.shared.infrastructure.web.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Access Denied Handler
 * <p>
 * Handles authenticated requests that lack sufficient permissions and
 * returns the standardized {@link ErrorResponse} JSON payload.
 * </p>
 */
@Component
public class UnauthorizedRequestHandlerAccessDenied implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedRequestHandlerAccessDenied.class);
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        LOGGER.warn("Access denied to {}: {}",
                request.getServletPath(), accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorResponse body = ErrorResponse.of(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                ErrorCode.ACCESS_DENIED,
                "You do not have permission to perform this action",
                request.getServletPath()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}
