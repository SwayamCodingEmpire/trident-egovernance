package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.BasicFeeBatchDetails;
import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.global.helpers.CfPaymentMode;
import com.trident.egovernance.global.helpers.TFWType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "FEES")
@Table(name = "FEES")
public class Fees implements Serializable {
    @Id
    private Long feeId;
    @Transient
    private BasicFeeBatchDetails batchElements;
    private String batchId;
    @NotNull
    private Integer regdYear;
    @Column(name = "DESCRIPTION")
    private String description;
    private BigDecimal amount;
    private String comments;
    @Enumerated(EnumType.STRING)
    private TFWType tfwType;
    private BigDecimal tatFees;
    private BigDecimal tactFfees;
    @Enumerated(EnumType.STRING)
    private CfPaymentMode payType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DESCRIPTION", referencedColumnName = "DESCRIPTION", insertable = false, updatable = false)
    private FeeTypes feeType;


}


