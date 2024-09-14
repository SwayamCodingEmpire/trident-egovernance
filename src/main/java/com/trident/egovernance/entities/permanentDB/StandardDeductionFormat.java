package com.trident.egovernance.entities.permanentDB;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "STANDARDDEDUCTIONFORMAT")
@Table(name = "STANDARDDEDUCTIONFORMAT")
public class StandardDeductionFormat {
    private Integer deductionOrder;
    @Id
    private String description;
}
