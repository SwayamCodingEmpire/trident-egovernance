package com.trident.egovernance.global.nav;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MenuNodeDto {
    private HashMap<String, Set<String>> titles;
    private List<String> urls;
    private String parentUrl;
}
