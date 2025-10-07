package com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.pipeline;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter responsible for extracting the Workshop ID from the Security Context (UserDetails)
 * and setting it into the WorkshopContext (ThreadLocal) for multi-tenancy.
 */
@Component
public class WorkshopExtractionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        WorkshopContext.clear();

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl userDetails) {

                Long workshopId = userDetails.getWorkshopId();

                if (workshopId != null) {
                    WorkshopContext.setCurrentWorkshopId(workshopId);
                }
            }
            filterChain.doFilter(request, response);

        } finally {
            WorkshopContext.clear();
        }
    }
}