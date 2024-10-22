package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "OLDADJUSTMENTS")
@Table(name = "OLDADJUSTMENTS")
public class OldAdjustments extends BaseAdjustments {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "old_adjustment_seq_gen")
    @SequenceGenerator(name = "old_adjustment_seq_gen", sequenceName = "old_adj_seq", allocationSize = 1)
    private Long id;
}
