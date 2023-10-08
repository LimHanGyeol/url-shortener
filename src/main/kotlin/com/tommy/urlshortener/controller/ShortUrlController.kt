package com.tommy.urlshortener.controller

import com.tommy.urlshortener.dto.RedirectResponseEntity
import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.service.UrlShortService
import com.tommy.urlshortener.service.UrlValidator
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ShortUrlController(
    private val urlValidator: UrlValidator,
    private val urlShortService: UrlShortService,
) {

    @PostMapping("/shorten")
    fun shortUrl(@RequestBody @Validated shortUrlRequest: ShortUrlRequest): ShortUrlResponse {
        urlValidator.validate(shortUrlRequest.originUrl)
        return urlShortService.shorten(shortUrlRequest)
    }

    @GetMapping("/{shortUrl}")
    fun redirect(@PathVariable shortUrl: String): RedirectResponseEntity {
        return RedirectResponseEntity("EysI9lHD")
    }
}
