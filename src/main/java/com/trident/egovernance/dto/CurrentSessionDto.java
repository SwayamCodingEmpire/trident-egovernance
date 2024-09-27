package com.trident.egovernance.dto;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrentSessionDto {
    private String regdNo;
    private String course;
    private int currentYear;
    private String sessionId;
    private String prevSessionId;
    private Date startDate;
}
