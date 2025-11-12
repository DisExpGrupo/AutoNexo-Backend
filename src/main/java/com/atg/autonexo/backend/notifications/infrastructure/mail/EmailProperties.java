package com.atg.autonexo.backend.notifications.infrastructure.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for email settings.
 */
@Component
@ConfigurationProperties(prefix = "autonexo.email")
@Getter
@Setter
public class EmailProperties {
    
    /**
     * Email address to use as sender (From field).
     */
    private String from = "noreply@autonexo.com";
    
    /**
     * Base URL of the application for generating links in emails.
     */
    private String baseUrl = "http://localhost:8080";
}

