package org.conservationco.asanahire.util

import reactor.core.publisher.Mono
import java.util.logging.Logger

internal fun handleSaveError(logger: Logger, it: Throwable): Mono<Void> {
    logger.severe("An error occurred while saving to repository: ${it.message})")
    return Mono.empty()
}
