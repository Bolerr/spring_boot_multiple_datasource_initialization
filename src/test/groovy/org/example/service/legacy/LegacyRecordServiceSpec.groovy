package org.example.service.legacy

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import org.example.legacy.domain.LegacyRecord
import org.example.legacy.repository.LegacyRepository
import spock.lang.Specification

class LegacyRecordServiceSpec extends Specification {

    @Collaborator
    LegacyRepository repository = Mock()

    @Subject
    LegacyRecordService service

    void "list() should return a collection of LegacyRecord objects"() {
        given:
        Collection<LegacyRecord> expectedResults = [
                new LegacyRecord(),
                new LegacyRecord()
        ]

        when:
        Collection<LegacyRecord> results = service.list()

        then:
        1 * repository.findAll() >> {
            return expectedResults
        }
        results.size() == expectedResults.size()
        results == expectedResults
    }

    void "get(Integer id) should return the LegacyRecord with that id"() {
        given:
        Integer requestedId = 1
        LegacyRecord expectedResult = new LegacyRecord(id: 1)

        when:
        LegacyRecord result = service.get(requestedId)

        then:
        1 * repository.findOne(requestedId) >> {
            return expectedResult
        }

        result == expectedResult
    }

    void "save should save a LegacyRecord"() {
        given:
        Integer id = 1
        LegacyRecord newRecord = new LegacyRecord(recordNumber: 1)

        when:
        LegacyRecord result = service.save(newRecord)

        then:
        1 * repository.save(newRecord) >> {
            newRecord.setId(id)
            return newRecord
        }
        result.id == id
    }

    void "save(Collection<LegacyRecord> entities) should save a collection of LegacyRecord objects"() {
        given:
        Collection<LegacyRecord> entities = [
                new LegacyRecord(id: 506, recordNumber: 1),
                new LegacyRecord(id: 505, recordNumber: 2)
        ]

        when:
        Collection<LegacyRecord> results = service.save(entities)

        then:
        1 * repository.save(entities) >> {
            entities.eachWithIndex { LegacyRecord entry, int i ->
                entry.setId((i + 1))
            }
            return entities
        }
        results.every { LegacyRecord obj -> obj.id }
    }

    void 'update(LegacyRecord entity) should save the updated entity'() {
        given: 'A valid entity'
        LegacyRecord entity = new LegacyRecord(
                id: Integer.MAX_VALUE,
                recordNumber: '1',
                content: 'content'
        )

        when: 'we call update'
        LegacyRecord updated = service.update(entity)

        then: 'we should see the entity get saved'
        1 * repository.save(entity) >> {
            return entity
        }
        updated == entity
    }

    void 'delete(Integer id) should delete the specified entity'() {
        given:
        Integer id = 1

        when: 'we call delete'
        service.delete(id)

        then: 'the entity should be deleted'
        1 * repository.delete(id) >> {}
    }
}
