package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.FeeTypeTypeConverter;
import com.trident.egovernance.helpers.FeeTypesType;
import com.trident.egovernance.helpers.MrHead;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "feeType",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Fees> fees;
}
