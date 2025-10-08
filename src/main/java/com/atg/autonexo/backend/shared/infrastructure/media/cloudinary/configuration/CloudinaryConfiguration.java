package com.atg.autonexo.backend.shared.infrastructure.media.cloudinary.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cloudinary Configuration
 * <p>
 * This class provides the configuration for Cloudinary integration,
 * allowing the application to upload and manage images in the cloud.
 * </p>
 */
@Configuration
public class CloudinaryConfiguration {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Creates and configures a Cloudinary instance
     * 
     * @return Configured Cloudinary instance
     */
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true));
    }
} 