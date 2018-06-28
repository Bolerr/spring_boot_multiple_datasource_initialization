package org.example.service

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import org.example.domain.Record
import org.example.domain.repository.RecordRepository
import spock.lang.Specification

class RecordServiceSpec extends Specification {

    @Collaborator
    RecordRepository repository = Mock()

    @Subject
    RecordService service

    void "list() should return a collection of Record objects"() {
        given:
        Collection<Record> expectedResults = [
                new Record(),
                new Record()
        ]

        when:
        Collection<Record> results = service.list()

        then:
        1 * repository.findAll() >> {
            return expectedResults
        }
        results.size() == expectedResults.size()
        results == expectedResults
    }

    void "get(Long id) should return the Record with that id"() {
        given:
        Long requestedId = 1
        Record expectedResult = new Record(id: requestedId)

        when:
        Record result = service.get(requestedId)

        then:
        1 * repository.findOne(requestedId) >> {
            return expectedResult
        }

        result == expectedResult
    }

    void "save should save a Record"() {
        given:
        Long id = 1
        Record newRecord = new Record(recordNumber: 1, content: 'test')

        when:
        Record result = service.save(newRecord)

        then:
        1 * repository.save(newRecord) >> {
            newRecord.setId(id)
            return newRecord
        }
        result.id == id
    }

    void "save(Collection<Record> entities) should save a collection of InstitutionProfileDetail objects"() {
        given:
        Collection<Record> entities = [
                new Record(id: 506, recordNumber: 1),
                new Record(id: 505, recordNumber: 2)
        ]

        when:
        Collection<Record> results = service.save(entities)

        then:
        1 * repository.save(entities) >> {
            entities.eachWithIndex { Record entry, int i ->
                entry.setId((i + 1))
            }
            return entities
        }
        results.every { Record obj -> obj.id }
    }

    void 'update(Record entity) should save the updated entity'() {
        given: 'A valid entity'
        Record entity = new Record(
                id: Long.MAX_VALUE,
                recordNumber: 1,
                content: 'test'
        )

        when: 'we call update'
        Record updated = service.update(entity)

        then: 'we should see the entity get saved'
        1 * repository.save(entity) >> {
            return entity
        }
        updated == entity
    }

    void 'delete(Long id) should delete the specified entity'() {
        given:
        Long id = 1

        when: 'we call delete'
        service.delete(id)

        then: 'the entity should be deleted'
        1 * repository.delete(id) >> {}
    }
}
