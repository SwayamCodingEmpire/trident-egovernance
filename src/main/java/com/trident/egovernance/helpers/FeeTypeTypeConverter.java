package com.trident.egovernance.helpers;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FeeTypeTypeConverter implements AttributeConverter<FeeTypesType,String> {
    @Override
    public String convertToDatabaseColumn(FeeTypesType attribute) {
        if(attribute==null){
            return null;
        }
        return attribute.getDisplayName();
    }

    @Override
    public FeeTypesType convertToEntityAttribute(String dbData) {
        if(dbData==null || dbData.isEmpty()){
            return null;
        }
        for(FeeTypesType feeTypesType:FeeTypesType.values()){
            if(feeTypesType.getDisplayName().equals(dbData)){
                return feeTypesType;
            }
        }
        throw new IllegalArgumentException("Unknown value: "+dbData);
    }
}
