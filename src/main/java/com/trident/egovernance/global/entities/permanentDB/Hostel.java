package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "HOSTEL")
@Table(name = "HOSTEL")
public final class Hostel extends BaseHostel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    private Student student; // VARCHAR2(100)
}
