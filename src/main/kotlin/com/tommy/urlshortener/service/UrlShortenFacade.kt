package com.tommy.urlshortener.service

import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.extension.HashAlgorithm
import com.tommy.urlshortener.extension.toHashedHex
import com.tommy.urlshortener.lock.DistributedLock
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class UrlShortenFacade(
    private val urlShortService: UrlShortService,
) {

    fun shortUrlWithLock(shortUrlRequest: ShortUrlRequest): ShortUrlResponse {
        val originUrl = shortUrlRequest.originUrl
        val hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256)

        return shortUrlWithLock(originUrl, hashedOriginUrl)
    }

    @DistributedLock(key = "#hashedOriginUrl", waitTime = 4L, leaseTime = 5L, timeUnit = TimeUnit.SECONDS)
    private fun shortUrlWithLock(originUrl: String, hashedOriginUrl: String): ShortUrlResponse {
        return urlShortService.shorten(originUrl, hashedOriginUrl)
    }
}
