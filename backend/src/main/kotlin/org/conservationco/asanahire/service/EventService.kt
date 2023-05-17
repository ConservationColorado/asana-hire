package org.conservationco.asanahire.service

import com.asana.models.Event
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.springframework.boot.json.JsonParseException
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class EventService(
    private val gson: Gson,
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
    internal fun processEvents(json: String) {
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
        if (events.isNotEmpty()) propogateEvents(events)
    }

    /**
     * Events are categorized by EventType
     *  - if the event is ADDED, it's an application project event
     *  - if the event is CHANGED, it's an interview project event
     */
    private fun propogateEvents(events: List<Event>) {
        // todo
    }

}
