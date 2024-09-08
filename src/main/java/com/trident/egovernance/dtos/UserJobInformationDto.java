package com.trident.egovernance.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserJobInformationDto {
    private String displayName;
    private String jobTitle;
    private String department;
}
