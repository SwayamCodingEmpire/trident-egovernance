package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "STAFFDESIGNATION")
@Table(name = "STAFFDESIGNATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StaffDesignationEntity {

    @Id
    @Column(name = "DES_ID")
    private String desId;

    @Column(name = "STAFF_DESIGNATION_NAME")
    private String staffDesignationName;

    @Column(name = "STAFF_DESIGNATION")
    private String staffDesignation;
}
