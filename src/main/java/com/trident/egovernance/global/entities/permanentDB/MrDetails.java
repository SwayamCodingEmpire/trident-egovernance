package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "MRDETAILS")
@Table(name = "MRDETAILS")
public class MrDetails {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mr_details_sequence")
    @SequenceGenerator(name = "mr_details_sequence",sequenceName = "MRDETAILS_SEQ",allocationSize = 1)
    private Long id;
//    @Column(name = "MRNO",insertable = false,updatable = false)
//    private Long mrNo;
    private long slNo;
    private String particulars;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "MRNO")
    @ToString.Exclude
    private FeeCollection feeCollection;
}
