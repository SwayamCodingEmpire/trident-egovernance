package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "PAYMENTMODE")
@Table(name = "PAYMENTMODE")
public class PaymentMode {
    @Id
    @Column(name = "PMO")
    private String pmo;
}
