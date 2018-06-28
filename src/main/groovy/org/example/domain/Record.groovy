package org.example.domain

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = 'record', schema = 'admin_records')
@Canonical
class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false, insertable = false, updatable = false)
    Long id

    @Column(name = 'record_number', nullable = false)
    @NotNull
    Integer recordNumber

    @Column(name = 'content', length = 255)
    String content
}
