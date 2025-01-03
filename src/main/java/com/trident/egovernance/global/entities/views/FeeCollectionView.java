package com.trident.egovernance.global.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trident.egovernance.dto.MrDetailsDTO;
import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.FeeProcessingMode;
import com.trident.egovernance.global.helpers.PaymentMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Set;

@Entity(name = "FEECOLLECTIONVIEW")
@Table(name = "FEECOLLECTIONVIEW")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
public class FeeCollectionView {
    @Id
    @Column(name = "MRNO", insertable=false, updatable=false)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mrno_seq_gen")
//    @SequenceGenerator(name = "mrno_seq_gen", sequenceName = "MRNO_SEQ", allocationSize = 1)
    private Long mrNo;
   @Column(name = "REGDNO",insertable = false,updatable = false)
    private String regdNo;
    @Column(name = "COLLECTEDFEE")
    private BigDecimal collectedFee;
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENTMODE")
    private PaymentMode paymentMode;
    @Column(name = "DDNO")
    private String ddNo;
    @Column(name = "DDDATE")
    private String ddDate;
    @Column(name = "DDBANK")
    private String ddBank;
    @Column(name = "PAYMENT_DATE")
    private Date paymentDate;
    @Column(name = "DUEYEAR")
    private int dueYear;
    @Column(name = "SESSIONID")
    private String sessionId;
    @Enumerated(EnumType.STRING)
    private FeeProcessingMode feeProcessingMode;
    @Transient
    private Set<MrDetailsDTO> mrDetailsDTOSet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO", insertable = false, updatable = false)
    @JsonIgnore
    private Student student;
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "feeCollectionView",fetch = FetchType.LAZY)
    private Set<MrDetails> mrDetails;
    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "feeCollectionView")
    private PaymentDuesDetails paymentDuesDetails;
}
