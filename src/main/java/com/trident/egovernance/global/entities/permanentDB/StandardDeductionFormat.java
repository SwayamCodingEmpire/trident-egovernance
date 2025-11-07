package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.FeeTypesWithDeductionOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STANDARDDEDUCTIONFORMAT")
@Table(name = "STANDARDDEDUCTIONFORMAT")
public class StandardDeductionFormat implements Serializable {
    private Integer deductionOrder;
    @Id
    @Column(name = "DESCRIPTION")
    private String description;

    public  StandardDeductionFormat(FeeTypesWithDeductionOrder  feeTypesWithDeductionOrder) {
        this.description = feeTypesWithDeductionOrder.description();
        this.deductionOrder = feeTypesWithDeductionOrder.deductionOrder();
    }

}