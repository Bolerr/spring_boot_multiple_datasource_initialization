package org.example

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@SpringBootTest
@Transactional
abstract class BaseRepositoryIntegrationSpec extends Specification {

    @PersistenceContext
    EntityManager entityManager

    //We have to clear out records both before and after tests due to BootStrapRunner polluting the database
    void cleanup() {
        entityManager.flush()
        deleteRecords()
    }

    void setup() {
        entityManager.flush()
        deleteRecords()
    }

    abstract void deleteRecords()
}
