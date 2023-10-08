package com.tommy.urlshortener.dto

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

class RedirectResponseEntity(location: String)
    : ResponseEntity<Any>(createHeadersWithLocation(location), HttpStatus.MOVED_PERMANENTLY) {

    companion object {
        private fun createHeadersWithLocation(location: String): HttpHeaders {
            return HttpHeaders().apply {
                this.location = URI.create(location)
            }
        }
    }
}

