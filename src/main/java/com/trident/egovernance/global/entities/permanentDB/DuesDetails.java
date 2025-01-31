package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.ExcessFeeStudentData;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

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

//    @Id
//    @Column(name = "ID")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dues_detail_seq_gen")
//    @SequenceGenerator(name = "dues_detail_seq_gen", sequenceName = "dues_detail_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DESCRIPTION", referencedColumnName = "DESCRIPTION", insertable = false, updatable = false)
    private FeeTypes feeType;

    public DuesDetails(ExcessRefund excessRefund, ExcessFeeStudentData excessFeeStudentData, StandardDeductionFormat standardDeductionFormat) {
        this.regdNo = excessFeeStudentData.regdNo();
        this.description = standardDeductionFormat.getDescription();
        this.id = -1L;
        setAmountDue(excessRefund.getRefundAmount());
        setAmountPaid(BigDecimal.ZERO);
        setBalanceAmount(excessRefund.getRefundAmount());
        setDeductionOrder(standardDeductionFormat.getDeductionOrder());
        setDueYear(excessFeeStudentData.regdyear());
        setSessionId(excessFeeStudentData.sessionId());
        setAmountPaidToJee(BigDecimal.ZERO);
        setDueDate(Date.valueOf(LocalDate.now()));
        setDiscount(BigDecimal.ZERO);
        setAdjustment(BigDecimal.ZERO);
    }

}
