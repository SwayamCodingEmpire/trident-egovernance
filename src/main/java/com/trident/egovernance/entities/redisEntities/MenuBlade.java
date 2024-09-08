package com.trident.egovernance.entities.redisEntities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash("MenuBlade")
public class MenuBlade implements Serializable {
    @Id
    private String job_title;
    private List<String> menu_blade;
}
