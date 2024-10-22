package com.trident.egovernance.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserJobInformationDto implements Serializable {
    private String displayName;
    private String jobTitle;
    private String department;
}
