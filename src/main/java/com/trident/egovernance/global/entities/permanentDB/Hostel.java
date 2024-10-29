package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.HostelOnlyDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "HOSTEL")
@Table(name = "HOSTEL")
public final class Hostel extends BaseHostel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGDNO")
    private Student student; // VARCHAR2(100)

    public Hostel(HostelOnlyDTO hostelOnlyDTO) {
        super(hostelOnlyDTO.regdNo(),
                hostelOnlyDTO.hostelier(),
                hostelOnlyDTO.hostelOption(),
                hostelOnlyDTO.hostelChoice(),
                hostelOnlyDTO.lgName(),
                hostelOnlyDTO.regdyear());
    }
    @Override
    public String toString() {
        return "Hostel{" +
                "regdNo='" + getRegdNo() + '\'' +
                ", hostelier=" + getHostelier() +
                ", hostelOption=" + getHostelOption() +
                ", hostelChoice=" + getHostelChoice() +
                ", lgName='" + getLgName() + '\'' +
                ", regdyear=" + getRegdyear() +
                ", student=" + (student != null ? student.toString() : "null") +
                '}';
    }
}
