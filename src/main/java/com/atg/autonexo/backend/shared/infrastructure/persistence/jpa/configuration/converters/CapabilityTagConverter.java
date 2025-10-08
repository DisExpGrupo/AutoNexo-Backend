package com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.configuration.converters;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for CapabilityTag enum to store as string in database
 */
@Converter(autoApply = true)
public class CapabilityTagConverter implements AttributeConverter<CapabilityTag, String> {

    @Override
    public String convertToDatabaseColumn(CapabilityTag tag) {
        return tag == null ? null : tag.name();
    }

    @Override
    public CapabilityTag convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return CapabilityTag.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CapabilityTag value in database: " + dbData, e);
        }
    }
}

