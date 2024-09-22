package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.BooleanString;
import com.trident.egovernance.helpers.HostelChoice;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "HOSTEL")
@Table(name = "HOSTEL")
public class Hostel {
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    private Student student; // VARCHAR2(100)
}
