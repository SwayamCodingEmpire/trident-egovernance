package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.TestTable;
import com.trident.egovernance.global.helpers.SessionIdId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestTableRepository extends JpaRepository<TestTable, SessionIdId> {
    List<TestTable> findAllByCourseOrderBySessionIdAscRegdYearAsc(String course);
}
