package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.MrDetailsDTOMinimal;
import com.trident.egovernance.global.entities.views.FeeCollectionView;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "MRDETAILS")
@Table(name = "MRDETAILS")
public class MrDetails {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mr_details_sequence")
    @SequenceGenerator(name = "mr_details_sequence",sequenceName = "MRDETAILS_SEQ",allocationSize = 1)
    private Long id;
//    @Column(name = "MRNO",insertable = false,updatable = false)
//    private Long mrNo;
    @Column(name = "SLNO")
    private long slNo;
    @Column(name = "PARTICULARS")
    private String particulars;
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MRNO")
    @ToString.Exclude
    private FeeCollection feeCollection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MRNO", insertable = false, updatable = false)
    @ToString.Exclude
    private FeeCollectionView feeCollectionView;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARTICULARS", referencedColumnName = "DESCRIPTION", insertable = false, updatable = false)
    private FeeTypes feeType;



    public MrDetails(MrDetailsDTOMinimal otherMrDetails, FeeCollection feeCollection) {
        this.slNo = otherMrDetails.slNo();
        this.particulars = otherMrDetails.particulars();
        this.amount = otherMrDetails.amount();
        this.feeCollection = feeCollection;
    }
}
