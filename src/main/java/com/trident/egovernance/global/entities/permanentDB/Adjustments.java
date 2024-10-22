package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "ADJUSTMENTS")
@Table(name = "ADJUSTMENTS")
public class Adjustments extends BaseAdjustments {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adjustment_seq_gen")
    @SequenceGenerator(name = "adjustment_seq_gen", sequenceName = "adj_seq", allocationSize = 1)
    private Long id;
}
