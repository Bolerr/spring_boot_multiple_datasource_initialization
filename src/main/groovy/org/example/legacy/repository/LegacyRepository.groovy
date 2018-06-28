package org.example.legacy.repository

import org.example.legacy.domain.LegacyRecord
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface LegacyRepository extends PagingAndSortingRepository<LegacyRecord, Integer> {

}