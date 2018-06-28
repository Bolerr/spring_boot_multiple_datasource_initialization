package org.example.service

import org.example.domain.Record
import org.example.domain.repository.RecordRepository
import org.springframework.stereotype.Service

@Service
class RecordService extends AbstractCrudService<Record, Long, RecordRepository> {

    RecordService(RecordRepository repository) {
        super(repository)
    }
}
