package com.trident.egovernance.dtos;

import com.trident.egovernance.annotations.ValidSize;
import com.trident.egovernance.helpers.AdmittedThrough;
import com.trident.egovernance.helpers.Courses;
import com.trident.egovernance.helpers.RankType;
import com.trident.egovernance.helpers.StudentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NSRDto {
    @NotBlank
    private String applicationNo;
    @NotBlank
    private String studentName;
    @ValidSize(message = "Roll No should be of 10 characters")
    private String rollNo;
    private Long rank;
    private RankType rankType;
    private Courses course;
    private Boolean TFW;
    private AdmittedThrough admittedThrough;
    private StudentType studentType;
}
