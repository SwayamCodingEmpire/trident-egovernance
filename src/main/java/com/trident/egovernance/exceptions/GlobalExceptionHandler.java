package com.trident.egovernance.exceptions;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.helpers.TFWType;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception){
        ProblemDetail problemDetail = null;
        exception.printStackTrace();
        if(exception instanceof BadCredentialsException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),exception.getMessage());
            problemDetail.setProperty("description","The Username or Password is incorrect");
            return problemDetail;
        }
        if(exception instanceof UnsupportedEncodingException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500),exception.getMessage());
            problemDetail.setProperty("description","Unable to fetch the data");
            return problemDetail;
        }
        if(exception instanceof AccountStatusException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),exception.getMessage());
            problemDetail.setProperty("description","The account is Locked");
            return problemDetail;
        }
        if(exception instanceof AccessDeniedException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),exception.getMessage());
            problemDetail.setProperty("description","You are not authorized to access this resource");
            return problemDetail;
        }
        if(exception instanceof SignatureException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),exception.getMessage());
            problemDetail.setProperty("description","The JWT Token has expired");
            return problemDetail;
        }
        if(exception instanceof RecordAlreadyExistsException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409),exception.getMessage());
            problemDetail.setProperty("description","The record already exists");
            return problemDetail;
        }
        if(exception instanceof RecordNotFoundException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),exception.getMessage());
            problemDetail.setProperty("description","The record not found");
            return problemDetail;
        }
        if(exception instanceof MessagingException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),exception.getMessage());
            problemDetail.setProperty("description","The record not found");
            return problemDetail;
        }
        if(exception instanceof InvalidInputsException){
            String[] errors = exception.getMessage().split("\\s*,\\s*");
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422),exception.getMessage());
            problemDetail.setProperty("description",errors);
            return problemDetail;
        }
        if(exception instanceof IOException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),exception.getMessage());
            problemDetail.setProperty("description","Unable to read");
            return problemDetail;
        }
        if(exception instanceof StreamReadException ){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),exception.getMessage());
            problemDetail.setProperty("description","Unable to read");
            return problemDetail;
        }
        if(exception instanceof DatabindException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),exception.getMessage());
            problemDetail.setProperty("description","Unable to read");
            return problemDetail;
        }
        if(exception instanceof HttpMessageNotReadableException){
            String input = exception.getMessage();
            Pattern classPattern = Pattern.compile("`(.*?)`");
            Matcher classMatcher = classPattern.matcher(input);
            String className = "";
            String shortClassName = "";
            if (classMatcher.find()) {
                className = classMatcher.group(1);  // Extracted class name
                String[] classParts = className.split("\\.");
                shortClassName = classParts[classParts.length - 1];
            }

            // Regular expression to extract the enum values (inside square brackets)
            Pattern enumPattern = Pattern.compile("\\[(.*?)\\]");
            Matcher enumMatcher = enumPattern.matcher(input);
            String enumValues = "";
            if (enumMatcher.find()) {
                enumValues = enumMatcher.group(1);  // Extracted enum values
            }

            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),enumValues);
            problemDetail.setProperty("description",shortClassName);
            return problemDetail;
        }

        if(exception instanceof IOException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),exception.getMessage());
            problemDetail.setProperty("description","The record not found");
            return problemDetail;
        }

            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500),exception.getMessage());
            problemDetail.setProperty("description","Unknown Internal Server Error");
            return problemDetail;
    }

    private List<String> extractInvalidTFWTypeDetails(Object invalidValue){
        List<String> invalidEntries = new ArrayList<>();
        logger.info("Inside extract");
        logger.info(invalidValue.toString());
        if(invalidValue instanceof List){
            List<Map<String,Object>> nsrList = (List<Map<String,Object>>) invalidValue;
            logger.info(invalidValue.toString());
            for(Map<String,Object> nsr : nsrList){
                logger.info(nsr.toString());
                String jeeRollNo = nsr.get("jeeApplicationNo").toString();
                logger.info(jeeRollNo);
                String tfwType = nsr.get("tfw").toString();
                logger.info(tfwType);
                if(!isValidTFWType(tfwType)){
                    logger.info(tfwType);
                    invalidEntries.add(jeeRollNo);
                }
            }
        }
        return invalidEntries;
    }

    private boolean isValidTFWType(String tfwType){
        try{
            TFWType.valueOf(tfwType.toUpperCase());
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }
}
