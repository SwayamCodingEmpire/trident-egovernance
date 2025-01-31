package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.PersonalDetailsOnlyDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "PERSONAL_DETAILS")
@Table(name = "PERSONAL_DETAILS")
public class PersonalDetails {
    @Id
    @Column(name = "REGDNO", length = 15)
    private String regdNo;  // VARCHAR2(15)

    @Column(name = "FNAME", length = 100)
    private String fname;  // VARCHAR2(100)

    @Column(name = "MNAME", length = 100)
    private String mname;  // VARCHAR2(100)

    @Column(name = "LGNAME", length = 100)
    private String lgName;  // VARCHAR2(100) - Assuming lgName refers to legal guardian name

    @Column(name = "PADDRESS", length = 500)
    private String permanentAddress;  // VARCHAR2(500)

    @Column(name = "PCITY", length = 50)
    private String permanentCity;  // VARCHAR2(50)

    @Column(name = "PSTATE", length = 50)
    private String permanentState;  // VARCHAR2(50)

    @Column(name = "PPINCODE", precision = 10)
    private Long permanentPincode;  // NUMBER(10,0)

    @Column(name = "PARENT_CONTACT", length = 50)
    private String parentContact;  // VARCHAR2(50)

    @Column(name = "PARENT_EMAILID", length = 50)
    private String parentEmailId;  // VARCHAR2(50)

    @Column(name = "PRESENT_ADDRESS", length = 500)
    private String presentAddress;  // VARCHAR2(500)

    @Column(name = "DISTRICT", length = 100)
    private String district;

    public PersonalDetails(PersonalDetailsOnlyDTO dto) {
        this.regdNo = dto.regdNo();
        this.fname = dto.fname();
        this.mname = dto.mname();
        this.lgName = dto.lgName();
        this.permanentAddress = dto.permanentAddress();
        this.permanentCity = dto.permanentCity();
        this.permanentState = dto.permanentState();
        this.permanentPincode = dto.permanentPincode();
        this.parentContact = dto.parentContact();
        this.parentEmailId = dto.parentEmailId();
        this.presentAddress = dto.presentAddress();
        this.district = dto.district();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    @MapsId
    @ToString.Exclude
    private Student student; // VARCHAR2(100)
}