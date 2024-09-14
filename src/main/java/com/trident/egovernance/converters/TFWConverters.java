package com.trident.egovernance.converters;

import com.trident.egovernance.helpers.TFWType;
import org.modelmapper.Converter;

public class TFWConverters {
    public static Converter<TFWType,String> enumToStringConverter(){
        return mappingContext -> mappingContext.getSource() == null ? null : mappingContext.getSource().name();
    }
}
