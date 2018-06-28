package org.example.legacy.repository

import org.example.BaseRepositoryIntegrationSpec
import org.example.legacy.domain.LegacyRecord
import org.springframework.beans.factory.annotation.Autowired

class LegacyRepositoryIntegrationSpec extends BaseRepositoryIntegrationSpec {

    @Autowired
    LegacyRepository repository

    void deleteRecords() {
        repository.deleteAll()
    }

    void "basic CRUD operations should work"() {
        given:
        LegacyRecord newObject = new LegacyRecord(
                recordNumber: '1',
                content: 'content'
        )

        when: 'object is created and read back from DB'
        LegacyRecord result = repository.save(newObject)
        LegacyRecord read = repository.findOne(result.id)
        Long savedId = read.id

        then: 'read object is correct'
        read.id
        read.content == 'content'

        when: 'item is updated'
        read.content = 'test'
        LegacyRecord updated = repository.save(read)
        LegacyRecord updateResult = repository.findOne(updated.id)

        then: 'item was updated correctly'
        updateResult.id == savedId
        updateResult.content == 'test'

        when: 'deleted'
        repository.delete(updateResult.id)
        LegacyRecord deleteResult = repository.findOne(updated.id)

        then: 'object is deleted and cannot be found'
        !deleteResult
    }
}