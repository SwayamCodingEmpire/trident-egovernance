package com.trident.egovernance.global.entities.views;

import com.trident.egovernance.global.helpers.DailyCollectionSummaryId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Table(name = "DAILYCOLLECTIONSUMMARY")
@Getter
@Setter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@IdClass(DailyCollectionSummaryId.class)
public class DailyCollectionSummary {
    @Id
    @Column(name = "PAYMENT_DATE")
    private String paymentDate;
    @Id
    @Column(name = "PAYMENTMODE")
    private String paymentMode;
    @Id
    @Column(name = "PARTICULARS")
    private String particulars;
    @Column(name = "TOTALAMOUNT")
    private BigDecimal totalAmount;
}