package com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.configuration.converters;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for ServiceCatalog enum to store as string in database
 */
@Converter(autoApply = true)
public class ServiceCatalogConverter implements AttributeConverter<ServiceCatalog, String> {

    @Override
    public String convertToDatabaseColumn(ServiceCatalog catalog) {
        return catalog == null ? null : catalog.name();
    }

    @Override
    public ServiceCatalog convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return ServiceCatalog.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ServiceCatalog value in database: " + dbData, e);
        }
    }
}

