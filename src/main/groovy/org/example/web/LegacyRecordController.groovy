package org.example.web

import groovy.util.logging.Slf4j
import io.swagger.annotations.Api
import org.example.legacy.domain.LegacyRecord
import org.example.service.legacy.LegacyRecordService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = '/api/v1.0/legacyRecord', produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = 'Example/v1.0/legacyRecord', description = 'Legacy Record resource')
@Slf4j
class LegacyRecordController {

    LegacyRecordService service

    LegacyRecordController(LegacyRecordService service) {
        this.service = service
    }

    /**
     * Retrieves a collect of LegacyRecord objects
     * @return {@link Collection < LegacyRecord >}
     */
    @RequestMapping(value = '/', method = RequestMethod.GET)
    ResponseEntity<Collection<LegacyRecord>> list() {
        Collection<LegacyRecord> results = service.list()
        return ResponseEntity.ok(results)
    }

    /**
     * Retrieves the requested LegacyRecord
     * @param id {@link Long}
     * @return {@link LegacyRecord}
     */
    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    ResponseEntity<LegacyRecord> get(@PathVariable('id') Integer id) {
        LegacyRecord result = service.get(id)
        if (result) {
            return ResponseEntity.ok(result)
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    /**
     * Updates the submitted LegacyRecord
     * @param updateObject {@link LegacyRecord}
     * @return {@link LegacyRecord}
     */
    @RequestMapping(value = '/{id}', method = RequestMethod.PUT)
    ResponseEntity<LegacyRecord> update(@RequestBody LegacyRecord updateObject) {
        log.debug("update($updateObject) called")

        return saveOrUpdate(updateObject)
    }

    /**
     * Saves a new submitted LegacyRecord
     * @param resource {@link LegacyRecord}
     * @return {@link LegacyRecord}
     */
    @RequestMapping(value = '/', method = RequestMethod.POST)
    ResponseEntity<LegacyRecord> saveNew(@RequestBody LegacyRecord resource) {
        log.debug("saveNew($resource) called")

        //For security safety, make sure id is not set
        resource.id = null

        return saveOrUpdate(resource)
    }

    /**
     * Deletes resource associated with id
     * @param id {@link Integer}
     * @return {@link Map < String , Integer >} id of deleted object
     */
    @RequestMapping(value = '/{id}', method = RequestMethod.DELETE)
    ResponseEntity<Map<String, Integer>> delete(@PathVariable('id') Integer id) {

        service.delete(id)
        return ResponseEntity.ok(['id': id])
    }

    protected ResponseEntity<LegacyRecord> saveOrUpdate(LegacyRecord object) {

        LegacyRecord updated = service.update(object)

        return ResponseEntity.ok(updated)
    }
}
