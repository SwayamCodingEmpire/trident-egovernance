package com.trident.egovernance.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MrDetailsDto {
    private Long mrNo;
    private Long id;
    private long slNo;
    private String particulars;
    private BigDecimal amount;
}
