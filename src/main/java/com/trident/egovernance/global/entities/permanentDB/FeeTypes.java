package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.FeeTypeTypeConverter;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.MrHead;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;
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
    @Min(0)
    private Integer semester;
    @OneToMany(mappedBy = "feeType",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Fees> fees;
    @OneToMany(mappedBy = "feeType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DuesDetails> duesDetails;

    @OneToMany(mappedBy = "feeType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<MrDetails> mrDetails;
}
