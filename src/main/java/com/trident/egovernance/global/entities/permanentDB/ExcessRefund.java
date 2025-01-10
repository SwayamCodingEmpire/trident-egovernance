package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.ExcessRefundDTO;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import com.trident.egovernance.global.helpers.ExcessRefundID;
import com.trident.egovernance.global.helpers.PaymentMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ExcessRefundID.class)
@Entity(name = "EXCESSREFUND")
@Table(name = "EXCESSREFUND")
public class ExcessRefund {
    @Id
    @Column(name = "REGDNO", length = 15, nullable = false)
    private String regdNo;

    @Column(name = "EXCESSFEEPAID", precision = 10, scale = 2)
    private BigDecimal excessFeePaid;

    @Column(name = "REFUNDAMOUNT", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "REFUNDMODE", length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMode refundMode;

    @Column(name = "CHQNO", length = 20)
    private String chqNo;

    @Column(name = "CHQDATE", length = 20)
    private String chqDate;

    @Id
    @Column(name = "VOUCHERNO", length = 20, nullable = false)
    private String voucherNo;

    @Column(name = "VOUCHERDATE", length = 20)
    private String voucherDate;

    @Column(name = "REGDYEAR", length = 20)
    private String regdYear;

    @Column(name = "SESSIONID", length = 20)
    private String sessionId;

    @Column(name = "PAYER")
    private String payer;

    public ExcessRefund(ExcessRefundDTO excessRefundDTO) {
        this.voucherNo = excessRefundDTO.voucherNo();
        this.regdNo = excessRefundDTO.regdNo();
        this.refundAmount = excessRefundDTO.refundAmount();
        this.refundMode = excessRefundDTO.refundMode();
        this.chqNo = excessRefundDTO.chqNo();
        this.chqDate = excessRefundDTO.chqDate();
    }
}
