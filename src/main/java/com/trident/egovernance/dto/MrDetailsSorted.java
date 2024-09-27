package com.trident.egovernance.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MrDetailsSorted {
    private List<MrDetailsDto> tat;
    private List<MrDetailsDto> tactF;
}
