package com.trident.egovernance.global.repositories.redisRepositories;

import com.trident.egovernance.global.entities.redisEntities.MenuBlade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuBladeRepository extends CrudRepository<MenuBlade,String> {
}
