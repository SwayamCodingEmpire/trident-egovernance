package com.trident.egovernance.global.helpers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
public class NSRDeserializer extends JsonDeserializer<NSR> {
    private final Logger logger = LoggerFactory.getLogger(NSRDeserializer.class);
    @Override
    public NSR deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        NSR nsr = jsonParser.getCodec().readValue(jsonParser, NSR.class);
        logger.info("Deserialized NSR: {}", nsr);
        // Validate TFWType
        validateEnumValue(nsr.getTfw(), TFWType.class, nsr.getJeeApplicationNo(), "TFWType");
        // Validate AnotherEnum (or other enums)
        validateEnumValue(nsr.getGender(), Gender.class, nsr.getJeeApplicationNo(), "Gender");
        validateEnumValue(nsr.getRankType(), RankType.class, nsr.getJeeApplicationNo(), "Gender");
        validateEnumValue(nsr.getCourse(), Courses.class, nsr.getJeeApplicationNo(), "Gender");
        validateEnumValue(nsr.getAdmissionType(), AdmissionType.class, nsr.getJeeApplicationNo(), "Gender");
        validateEnumValue(nsr.getStudentType(), StudentType.class, nsr.getJeeApplicationNo(), "Gender");
//        validateEnumValue(nsr.getBranchCode(), Br.class, nsr.getJeeApplicationNo(), "Gender");
        validateEnumValue(nsr.getOjeeCouncellingFeePaid(), BooleanString.class, nsr.getJeeApplicationNo(), "Ojee Councelling Fee Paid");

        return nsr;
    }

    private <E extends Enum<E>> void validateEnumValue(E value, Class<E> enumClass, String applicationNo, String fieldName) throws JsonProcessingException {
        logger.info("Validate Enum Value: {}", value);
        if (value == null) {
            throw new JsonProcessingException(fieldName + " cannot be null for application number: " + applicationNo) {};
        }

        try {
            Enum.valueOf(enumClass, value.name());
        } catch (IllegalArgumentException e) {
            throw new JsonProcessingException("Invalid " + fieldName + " value: " + value + " for application number: " + applicationNo) {};
        }
    }
}
