package org.example.service.legacy

import org.example.legacy.domain.LegacyRecord
import org.example.legacy.repository.LegacyRepository
import org.example.service.AbstractCrudService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LegacyRecordService extends AbstractCrudService<LegacyRecord, Integer, LegacyRepository> {

    @Autowired
    LegacyRecordService(LegacyRepository repository) {
        super(repository)
    }
}
