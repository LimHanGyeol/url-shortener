package com.tommy.urlshortener.service

import com.tommy.urlshortener.dto.OriginUrlResponse
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UrlRedirectService(
    private val shortenUrlRepository: ShortenUrlRepository,
) {
    private val logger = KotlinLogging.logger { }

    fun findOriginUrl(shortUrl: String): OriginUrlResponse {
        // TODO: Cache 조회 후 없으면 DB Find
        logger.debug { "shortUrl: [$shortUrl]" }
        val shortenUrl = shortenUrlRepository.findByShortUrl(shortUrl) ?: throw RuntimeException() // TODO: NotFoundException

        return OriginUrlResponse(shortenUrl.originUrl)
    }
}
