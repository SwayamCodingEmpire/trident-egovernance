package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.NotPromotedId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@IdClass(NotPromotedId.class)
@Entity(name = "NOTPROMOTED")
@Table(name = "NOTPROMOTED")
public class Notpromoted {
    @Size(max = 12)
    @Column(name = "REGDNO", length = 12)
    @Id
    private String regdNo;

    @Column(name = "CURRENTYEAR")
    @Id
    private Integer currentYear;

    @Size(max = 15)
    @Column(name = "SESSIONID", length = 15)
    @Id
    private String sessionId;

}