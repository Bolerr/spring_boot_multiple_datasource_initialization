package org.example.legacy.domain

import groovy.transform.Canonical

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = 'record', schema = 'dbo')
@Canonical
class LegacyRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'recordId', nullable = false, insertable = false, updatable = false)
    Integer id

    @Column(name = 'recordNumber', length = 5, nullable = false)
    @NotNull
    String recordNumber

    @Column(name = 'content', length = 255)
    String content
}
