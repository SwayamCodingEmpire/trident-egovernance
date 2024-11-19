package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.DuesDetailsId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(DuesDetailsId.class)
@Entity(name = "DUESDETAIL")
@Table(name = "DUESDETAIL")
public final class DuesDetails extends BaseDuesDetails {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Id
    @Column(name = "DESCRIPTION",length = 15)
    private String description;

    @Id
//    @Column(name = "ID")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dues_detail_seq_gen")
//    @SequenceGenerator(name = "dues_detail_seq_gen", sequenceName = "dues_detail_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DESCRIPTION", referencedColumnName = "DESCRIPTION", insertable = false, updatable = false)
    private FeeTypes feeType;

}
