package com.trident.egovernance.repositories.nsrRepositories;

import com.trident.egovernance.entities.redisEntities.MenuBlade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuBladeRepository extends CrudRepository<MenuBlade,String> {
}
