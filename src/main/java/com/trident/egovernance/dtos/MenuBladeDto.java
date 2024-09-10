package com.trident.egovernance.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MenuBladeDto implements Serializable {
    private List<String> menuBlade;
}
