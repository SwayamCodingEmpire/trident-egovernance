package com.trident.egovernance.global.helpers;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class FeeTypeTypeConverter implements AttributeConverter<FeeTypesType,String> {
    private final Logger logger = LoggerFactory.getLogger(FeeTypeTypeConverter.class);
    @Override
    public String convertToDatabaseColumn(FeeTypesType attribute) {
        logger.info("Converting to database column FeetypeConverter: {}", attribute);
        if(attribute==null){
            return null;
        }
        return attribute.getDisplayName();
    }

    @Override
    public FeeTypesType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        FeeTypesType type = FeeTypesType.fromDisplayName(dbData);
        if (type == null) {
            logger.error("Failed to convert database value to enum: {}", dbData);
            throw new IllegalArgumentException("Unknown value: " + dbData);
        }
        return type;
    }

}
