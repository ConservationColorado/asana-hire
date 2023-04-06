package org.conservationco.asanahire.exception

import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException::class)
    fun handleJobNotFoundException(ex: JobNotFoundException) =
        ErrorResponse.builder(
            ex,
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.message.toString()
        ).build()

    @ExceptionHandler(SyncNotFoundException::class)
    fun handleSyncNotFoundException(ex: SyncNotFoundException) =
        ErrorResponse.builder(
            ex,
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.message.toString()
        ).build()

}
