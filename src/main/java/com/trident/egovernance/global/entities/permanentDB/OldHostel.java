package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity(name = "OLDHOSTEL")
@Table(name = "OLDHOSTEL")
public final class OldHostel extends BaseHostel {
}
