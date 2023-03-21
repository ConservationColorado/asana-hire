package org.conservationco.asanahire.service

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import org.conservationco.asanahire.controller.AuthController
import org.conservationco.asanahire.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class AuthService(
    private val userRepository: UserRepository,
    @Autowired private val authorizationFlow: GoogleAuthorizationCodeFlow,
    @Autowired private val transport: HttpTransport,
    @Autowired private val jsonFactory: JsonFactory,
    @Autowired private val tokenVerifier: GoogleIdTokenVerifier,
) {

    private val logger = Logger.getLogger(AuthController::class.java.name)

    fun checkAgainstGoogle(code: String): Boolean {
        val credential = authorizationFlow.newTokenRequest(code)
        val token = credential.execute()
        val parsed = token.parseIdToken()
        return tokenVerifier.verify(parsed)
    }

    fun validateCode(code: String): ResponseEntity<*> {
        val response = checkAgainstGoogle(code)
        return ResponseEntity(response, HttpStatus.FORBIDDEN)
    }

}