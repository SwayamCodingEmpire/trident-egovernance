package com.trident.egovernance.global.helpers;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Converter(autoApply = true)
public class CourseConverter implements AttributeConverter<Courses,String> {
    private final Logger logger = LoggerFactory.getLogger(CourseConverter.class);

    @Override
    public String convertToDatabaseColumn(Courses attribute) {
        if(attribute==null){
            logger.error("attribute is null");
            return null;
        }
        logger.info(attribute.toString());
        logger.info(attribute.getDisplayName());
        return attribute.getDisplayName();
    }

    @Override
    public Courses convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            logger.info("dbData is null or empty");
            return null;
        }
        Courses course = Courses.fromDisplayName(dbData);
        if (course == null) {
            throw new IllegalArgumentException("Unknown value: " + dbData);
        }
        return course;
    }
}
