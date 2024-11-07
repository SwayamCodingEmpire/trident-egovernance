package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.DuesDetailsId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(DuesDetailsId.class)
@Entity(name = "OLDDUEDDETAIL")
@Table(name = "OLDDUEDDETAIL")
public final class OldDueDetails extends BaseDuesDetails {
    @Id
    @Column(name = "REGDNO")
    private String regdNo;
    @Id
    @Column(name = "DESCRIPTION",length = 15)
    private String description;

    public OldDueDetails(DuesDetails duesDetails) {
        super(duesDetails);
        this.regdNo = duesDetails.getRegdNo();
        this.description = duesDetails.getDescription();
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "old_dues_detail_seq_gen")
    @SequenceGenerator(name = "old_dues_detail_seq_gen", sequenceName = "oldDueDetails_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DESCRIPTION", referencedColumnName = "DESCRIPTION", insertable = false, updatable = false)
    private FeeTypes feeType;
}
