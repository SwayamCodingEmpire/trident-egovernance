package com.trident.egovernance.entities.redisEntities;

import com.trident.egovernance.helpers.AdmittedThrough;
import com.trident.egovernance.helpers.Courses;
import com.trident.egovernance.helpers.RankType;
import com.trident.egovernance.helpers.StudentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@RedisHash("NSR")
public class NSR implements Serializable {
    @Id
    private String applicationNo;
    private String studentName;
    private String rollNo;
    private Long rank;
    private RankType rankType;
    private Courses course;
    private Boolean TFW;
    private AdmittedThrough admittedThrough;
    private StudentType studentType;
}
