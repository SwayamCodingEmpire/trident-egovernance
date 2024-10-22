package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "DISCOUNT")
@Table(name = "DISCOUNT")
public final class Discount extends BaseDiscount {

    @Transient
    private int currentYear;
}
