package com.trident.egovernance.global.entities.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * Mapping for DB view
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Immutable
@Table(name = "ADJUSTMENT")
public class Adjustment {
    @Size(max = 15)
    @Column(name = "REGDNO", length = 15)
    @Id
    private String regdno;

    @Column(name = "REGDYEAR")
    private Short regdyear;

    @Size(max = 500)
    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "CONSIDERATIONAMOUNT", precision = 10, scale = 2)
    private BigDecimal considerationamount;

    @Size(max = 500)
    @Column(name = "APPROVEDBY", length = 500)
    private String approvedby;

}