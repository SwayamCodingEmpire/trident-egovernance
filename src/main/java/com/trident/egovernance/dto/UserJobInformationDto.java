package com.trident.egovernance.dto;

import lombok.*;

import java.io.Serializable;


public record UserJobInformationDto(
        String displayName,
        String jobTitle,
        String department,
        String employeeId
) implements Serializable{

}
