package com.trident.egovernance.helpers;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Date;

public class DateConverters {
    public static Converter<java.util.Date,java.sql.Date> utilToSqlConverter(){
        return new Converter<Date, java.sql.Date>() {
            @Override
            public java.sql.Date convert(MappingContext<Date, java.sql.Date> mappingContext) {
                return mappingContext.getSource() == null ? null : new java.sql.Date(mappingContext.getSource().getTime());
            }
        };
    }
    public static Converter<java.sql.Date,java.util.Date> sqlToUtilConverter(){
        return new Converter<java.sql.Date, Date>() {
            @Override
            public Date convert(MappingContext<java.sql.Date, Date> mappingContext) {
                return mappingContext.getSource() == null ? null : new Date(mappingContext.getSource().getTime());
            }
        };
    }
}
