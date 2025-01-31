package com.trident.egovernance.global.entities.permanentDB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.trident.egovernance.dto.FeeTypesOnly;
import com.trident.egovernance.global.helpers.FeeTypeTypeConverter;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.MrHead;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "FEETYPES")
@Table(name = "FEETYPES")
public class FeeTypes implements Serializable {
    @Id
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TYPE")
    @Convert(converter = FeeTypeTypeConverter.class)
    private FeeTypesType type;
    @Column(name = "FEEGROUP")
    private String feeGroup;
    @Column(name = "MRHEAD")
    @Enumerated(EnumType.STRING)
    private MrHead mrHead;
    @Column(name = "PARTOF")
    private String partOf;
    @Column(name = "SEM")
    private Integer semester;
    @OneToMany(mappedBy = "feeType", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Fees> fees;
    @OneToMany(mappedBy = "feeType",  fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<DuesDetails> duesDetails;

    @ToString.Exclude
    @OneToMany(mappedBy = "feeType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MrDetails> mrDetails;

    public FeeTypes(FeeTypesOnly feeTypesOnly){
        this.description=feeTypesOnly.description();
        this.type=feeTypesOnly.type();
        this.feeGroup=feeTypesOnly.feeGroup();
        this.mrHead=feeTypesOnly.mrHead();
        this.partOf=feeTypesOnly.partOf();
        this.semester=feeTypesOnly.semester() == null ? -1 : feeTypesOnly.semester();
    }
}
