package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.HostelChoice;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract class BaseHostel {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Column(name = "HOSTELIER")
    @Enumerated(EnumType.STRING)
    private BooleanString hostelier;
    @Enumerated(EnumType.STRING)
    @Column(name = "HOSTELOPTION")
    private BooleanString hostelOption;
    @Column(name = "HOSTELCHOICE")
    @Enumerated(EnumType.STRING)
    private HostelChoice hostelChoice;
    @Column(name = "LGNAME")
    private String lgName;
    @Column(name = "REGDYEAR")
    private Integer regdyear;

}
