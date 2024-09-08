package com.trident.egovernance.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Login {
    private String applicationNo;
    private Long rank;
}
