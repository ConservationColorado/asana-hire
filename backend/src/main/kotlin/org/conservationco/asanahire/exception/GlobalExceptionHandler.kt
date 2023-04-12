package org.conservationco.asanahire.exception

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
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

    @ExceptionHandler(GoogleJsonResponseException::class)
    fun handleGoogleJsonResponseException(ex: GoogleJsonResponseException) =
        ErrorResponse.builder(
            ex,
            HttpStatus.FORBIDDEN,
            ex.message.toString()
        ).build()

    @ExceptionHandler(OAuth2AuthenticationException::class)
    fun handleOAuth2AuthenticationException(ex: OAuth2AuthenticationException) =
        ErrorResponse.builder(
            ex,
            HttpStatus.FORBIDDEN,
            ex.message.toString()
        ).build()

}
