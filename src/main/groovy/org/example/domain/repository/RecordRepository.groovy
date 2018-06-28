package org.example.domain.repository

import org.example.domain.Record
import org.springframework.data.repository.PagingAndSortingRepository

interface RecordRepository extends PagingAndSortingRepository<Record, Long> {

}