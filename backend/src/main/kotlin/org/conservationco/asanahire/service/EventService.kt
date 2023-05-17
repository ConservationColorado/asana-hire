package org.conservationco.asanahire.service

import com.asana.models.Event
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.model.asana.ApplicantSyncPair
import org.conservationco.asanahire.util.convertToManagerApplicant
import org.conservationco.asanahire.util.convertToOriginalApplicant
import org.springframework.boot.json.JsonParseException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Service
class EventService(
    private val gson: Gson,
    private val syncService: SyncService,
    private val jobService: JobService,
) {

    private val logger = Logger.getLogger(EventService::class.qualifiedName)

    /**
     * If [json] is not empty or blank, then it will take on this format:
     *
     * ```
     * {
     *     "events": [
     *         { com.asana.Event object }, { com.asana.Event object }, { com.asana.Event object }
     *     ]
     * }
     * ```
     */
    internal fun processEvents(json: String): Mono<ResponseEntity<String>> {
        val events: List<Event> = try {
            gson
                .fromJson(json, JsonElement::class.java)
                .asJsonObject
                .get("events")
                .asJsonArray
                .map { gson.fromJson(it, Event::class.java) }
        } catch (e: JsonParseException) {
            logger.severe("Could not parse the given JSON: $json")
            emptyList()
        }
        return if (events.isNotEmpty()) propogateEvents(events)
        else Mono.empty()
    }

    /**
     * Events are categorized by EventType
     *  - if the event is ADDED, it's an application project event
     *  - if the event is CHANGED, it's an interview project event
     */
    private fun propogateEvents(events: List<Event>): Mono<ResponseEntity<String>> = asanaContext {
        val taskEvents = events.filter { it.resource?.resourceType == "task" }
        val (addedEvents, rest) = taskEvents.partition { it.action == "added" }
        val (changedEvents, _) = rest.partition { it.action == "changed" }

        return handleAddedEvents(addedEvents)
    }

    private fun handleAddedEvents(addedEvents: List<Event>): Mono<ResponseEntity<String>> = asanaContext {
        if (addedEvents.isEmpty()) return Mono.empty()

        val newApplicantsToSync =
            addedEvents
                .asSequence()
                .distinctBy { it.resource?.gid }
                .map { it.resource.gid }
                .map { task(it).get(includeAttachments = true) }
                .map {
                    ApplicantSyncPair(
                        it.convertToOriginalApplicant(),
                        it.convertToManagerApplicant()
                    )
                }
                .toList()

        val applicationProjectId = addedEvents.find { it.parent.resourceType == "project" }?.parent?.gid
        if (applicationProjectId.isNullOrEmpty()) return Mono.just(ResponseEntity.noContent().build())
        else return jobService
            .findJobOrFetch(applicationProjectId)
            .flatMap {
                Flux.fromIterable(newApplicantsToSync)
                    .flatMap { application ->
                        syncService.syncSingleApplicant(
                            application,
                            project(it.interviewProjectId),
                            it.title,
                            project(it.applicationProjectId)
                        )
                    }
                    .onErrorResume {
                        logger.severe("Could not sync applicants $newApplicantsToSync")
                        Mono.empty()
                    }
                    .then(Mono.just(ResponseEntity.ok().build()))
            }

    }

}
