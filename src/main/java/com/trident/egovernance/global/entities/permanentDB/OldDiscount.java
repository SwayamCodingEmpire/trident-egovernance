package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "OLDDISCOUNT")
@Table(name = "OLDDISCOUNT")
public final class OldDiscount extends BaseDiscount {
}