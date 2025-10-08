package com.atg.autonexo.backend.shared.infrastructure.media.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Cloudinary Service Interface
 * <p>
 * This interface defines the contract for Cloudinary operations,
 * including file upload, deletion, and URL generation.
 * </p>
 */
public interface CloudinaryService {

    /**
     * Upload a file to Cloudinary
     *
     * @param file The multipart file to upload
     * @param folder The folder where the file should be stored (optional)
     * @return Upload result containing URL and public ID
     * @throws RuntimeException if upload fails
     */
    Map<String, Object> uploadFile(MultipartFile file, String folder);

    /**
     * Upload a file to Cloudinary with default folder
     *
     * @param file The multipart file to upload
     * @return Upload result containing URL and public ID
     * @throws RuntimeException if upload fails
     */
    Map<String, Object> uploadFile(MultipartFile file);

    /**
     * Delete a file from Cloudinary
     *
     * @param publicId The public ID of the file to delete
     * @return Deletion result
     * @throws RuntimeException if deletion fails
     */
    Map<String, Object> deleteFile(String publicId);

    /**
     * Generate a secure URL for a file with transformations
     *
     * @param publicId The public ID of the file
     * @param width The desired width (optional)
     * @param height The desired height (optional)
     * @param quality The desired quality (optional)
     * @return The secure URL with transformations
     */
    String generateSecureUrl(String publicId, Integer width, Integer height, String quality);

    /**
     * Generate a secure URL for a file without transformations
     *
     * @param publicId The public ID of the file
     * @return The secure URL
     */
    String generateSecureUrl(String publicId);
} 