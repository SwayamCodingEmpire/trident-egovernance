package com.trident.egovernance.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class StudentCountDto {
    private Long continuingStudentCount;
    private Long AlumniStudentCount;
}
