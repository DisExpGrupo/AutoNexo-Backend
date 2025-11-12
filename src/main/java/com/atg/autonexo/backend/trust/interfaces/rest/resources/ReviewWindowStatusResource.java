package com.atg.autonexo.backend.trust.interfaces.rest.resources;

/**
 * Resource representing review window status.
 */
public record ReviewWindowStatusResource(
    boolean canReview,
    String reason,
    boolean reviewExists,
    boolean windowExpired,
    boolean serviceNotCompleted
) {}

