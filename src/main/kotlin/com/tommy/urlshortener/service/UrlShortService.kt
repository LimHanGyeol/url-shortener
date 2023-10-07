package com.tommy.urlshortener.service

import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class UrlShortService(
    private val shortenKeyGenerator: ShortenKeyGenerator,
    private val shortUrlGenerator: ShortUrlGenerator,
    private val shortenUrlRepository: ShortenUrlRepository,
) {
    private val logger = KotlinLogging.logger { }

    @Transactional
    fun shorten(shortUrlRequest: ShortUrlRequest): ShortUrlResponse {
        val originUrl = shortUrlRequest.originUrl

        // TODO: Cache 조회 후 없으면 DB Find
        val shortenUrl = shortenUrlRepository.findByOriginUrl(originUrl)

        val shortUrl = shortenUrl?.shortUrl ?: createShortenUrl(originUrl)
        logger.debug { "originUrl: [$originUrl], shortUrl: [$shortUrl]" }

        return ShortUrlResponse(shortUrl)
    }

    private fun createShortenUrl(originUrl: String): String {
        val timestamp = Instant.now().epochSecond

        val shortenKey = shortenKeyGenerator.generate(timestamp)
        val generatedShortUrl = shortUrlGenerator.generate(shortenKey)

        val shortenUrl = shortenUrlRepository.save(
            ShortenUrl(shortenKey = shortenKey, originUrl = originUrl, shortUrl = generatedShortUrl)
        )
        return shortenUrl.shortUrl
    }
}
