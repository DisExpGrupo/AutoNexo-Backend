package com.atg.autonexo.backend.shared.infrastructure.media.cloudinary.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import com.atg.autonexo.backend.shared.infrastructure.media.cloudinary.CloudinaryService;

/**
 * Cloudinary Service Implementation
 * <p>
 * This class implements the CloudinaryService interface and provides
 * concrete implementations for file upload, deletion, and URL generation
 * operations using the Cloudinary service.
 * </p>
 */
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    
    @Value("${cloudinary.base-url}")
    private String baseUrl;

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload a file to Cloudinary with specified folder
     *
     * @param file The multipart file to upload
     * @param folder The folder where the file should be stored
     * @return Upload result containing URL and public ID
     * @throws RuntimeException if upload fails
     */
    @Override
    public Map<String, Object> uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        try {
            LOGGER.info("Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);
            
            Map<String, Object> options = ObjectUtils.asMap(
                "folder", baseUrl + "/" + folder,
                "resource_type", "auto",
                "use_filename", true,
                "unique_filename", true
            );

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);
            
            LOGGER.info("File uploaded successfully. Public ID: {}", result.get("public_id"));
            return result;

        } catch (IOException e) {
            LOGGER.error("Error uploading file to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    /**
     * Upload a file to Cloudinary with default folder
     *
     * @param file The multipart file to upload
     * @return Upload result containing URL and public ID
     * @throws RuntimeException if upload fails
     */
    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        return uploadFile(file, baseUrl);
    }

    /**
     * Delete a file from Cloudinary
     *
     * @param publicId The public ID of the file to delete
     * @return Deletion result
     * @throws RuntimeException if deletion fails
     */
    @Override
    public Map<String, Object> deleteFile(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new IllegalArgumentException("Public ID cannot be null or empty");
        }

        try {
            LOGGER.info("Deleting file with public ID: {}", publicId);
            
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            
            LOGGER.info("File deleted successfully. Result: {}", result.get("result"));
            return result;

        } catch (IOException e) {
            LOGGER.error("Error deleting file from Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }

    /**
     * Generate a secure URL for a file with transformations
     *
     * @param publicId The public ID of the file
     * @param width The desired width (optional)
     * @param height The desired height (optional)
     * @param quality The desired quality (optional)
     * @return The secure URL with transformations
     */
    @Override
    public String generateSecureUrl(String publicId, Integer width, Integer height, String quality) {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new IllegalArgumentException("Public ID cannot be null or empty");
        }

        try {
            LOGGER.debug("Generating secure URL for public ID: {} with transformations", publicId);
            
            Transformation transformation = new Transformation();
            
            if (width != null) {
                transformation.width(width);
            }
            if (height != null) {
                transformation.height(height);
            }
            if (quality != null && !quality.trim().isEmpty()) {
                transformation.quality(quality);
            }

            String url = cloudinary.url()
                .secure(true)
                .transformation(transformation)
                .generate(publicId);
            
            LOGGER.debug("Generated secure URL: {}", url);
            return url;

        } catch (Exception e) {
            LOGGER.error("Error generating secure URL for public ID {}: {}", publicId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate secure URL", e);
        }
    }

    /**
     * Generate a secure URL for a file without transformations
     *
     * @param publicId The public ID of the file
     * @return The secure URL
     */
    @Override
    public String generateSecureUrl(String publicId) {
        return generateSecureUrl(publicId, null, null, null);
    }
} 