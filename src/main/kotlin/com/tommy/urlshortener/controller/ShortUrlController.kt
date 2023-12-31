package com.tommy.urlshortener.controller

import com.tommy.urlshortener.aop.MeasurePerformance
import com.tommy.urlshortener.dto.RedirectResponseEntity
import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.service.UrlRedirectService
import com.tommy.urlshortener.service.UrlShortenFacade
import com.tommy.urlshortener.service.UrlValidator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
class ShortUrlController(
    private val urlValidator: UrlValidator,
    private val urlShortenFacade: UrlShortenFacade,
    private val urlRedirectService: UrlRedirectService,
) {

    @MeasurePerformance
    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    fun shortUrl(@RequestBody @Valid shortUrlRequest: ShortUrlRequest): ShortUrlResponse {
        urlValidator.validate(shortUrlRequest.originUrl)
        return urlShortenFacade.shortUrlWithLock(shortUrlRequest)
    }

    @GetMapping("/redirect/{shortUrl}")
    fun redirect(@PathVariable shortUrl: String): RedirectResponseEntity {
        val originUrlResponse = urlRedirectService.findOriginUrl(shortUrl)
        return RedirectResponseEntity(originUrlResponse.originUrl)
    }
}
