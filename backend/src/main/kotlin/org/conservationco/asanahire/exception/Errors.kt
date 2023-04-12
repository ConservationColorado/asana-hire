package org.conservationco.asanahire.exception

import reactor.core.publisher.Mono
import java.util.logging.Logger

internal fun <T> handleSaveError(logger: Logger, error: Throwable): Mono<T> {
    logger.severe("An error occurred while saving to repository: ${error.message})")
    return Mono.empty()
}
