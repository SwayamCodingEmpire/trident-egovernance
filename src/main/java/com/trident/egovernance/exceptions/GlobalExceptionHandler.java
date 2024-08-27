package com.trident.egovernance.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public ProblemDetail handleException(Exception exception){
        ProblemDetail problemDetail = null;
        exception.printStackTrace();
        if(exception instanceof BadCredentialsException){
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),exception.getMessage());
            problemDetail.setProperty("description","The Username or Password is incorrect");
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
        else {
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500),exception.getMessage());
            problemDetail.setProperty("description","Unknown Internal Server Error");
            return problemDetail;
        }
    }
}
