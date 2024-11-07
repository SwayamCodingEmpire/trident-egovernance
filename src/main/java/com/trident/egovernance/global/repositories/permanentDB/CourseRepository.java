package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {
    @Modifying
    @Transactional
    @Query(value = """
        DECLARE
            c_name VARCHAR2(255);
            t_name VARCHAR2(255);
        BEGIN
            FOR c IN (SELECT constraint_name, table_name
                      FROM user_constraints
                      WHERE constraint_type IN ('R', 'U', 'C'))
            LOOP
                c_name := c.constraint_name;
                t_name := c.table_name;
                EXECUTE IMMEDIATE 'ALTER TABLE ' || t_name || ' ENABLE CONSTRAINT ' || c_name;
            END LOOP;
        END;
        """, nativeQuery = true)
    void enableAllConstraints();

    @Modifying
    @Transactional
    @Query(value = """
        DECLARE
            c_name VARCHAR2(255);
            t_name VARCHAR2(255);
        BEGIN
            FOR c IN (SELECT constraint_name, table_name
                      FROM user_constraints
                      WHERE constraint_type IN ('R', 'U', 'C'))
            LOOP
                c_name := c.constraint_name;
                t_name := c.table_name;
                EXECUTE IMMEDIATE 'ALTER TABLE ' || t_name || ' DISABLE CONSTRAINT ' || c_name;
            END LOOP;
        END;
        """, nativeQuery = true)
    void disableAllConstraints();
}
