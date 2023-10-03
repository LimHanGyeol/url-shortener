package com.tommy.urlshortener.controller

import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ShortUrlController {

    @PostMapping("/shorten")
    fun shortUrl(@RequestBody @Validated shortUrlRequest: ShortUrlRequest): ShortUrlResponse {
        return ShortUrlResponse("")
    }
}
