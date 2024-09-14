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
@Entity(name = "FEETYPES")
@Table(name = "FEETYPES")
public class FeeTypes {
    @Id
    private String description;
    private String type;
    private String feeGroup;
    private String mrHead;

}
