package com.trident.egovernance.global.entities.redisEntities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash("MenuBlade")
public class MenuBlade implements Serializable {
    @Id
    private String jobTitle;
    private HashMap<String,List<String>> parentRoutes;
}
