package com.trident.egovernance.global.nav;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AllowedUrls {
    private List<String> urls;
}
