package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.security.SecureRandom;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "BRANCH")
@Table(name = "BRANCH")
public class Branch {
    @Id
    private String branchCode;
    private String branch;
    private String course;
    private String duration;
    private Integer courseInProgress;
    private String studentType;
}
