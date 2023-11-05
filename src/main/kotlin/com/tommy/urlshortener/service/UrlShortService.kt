package com.tommy.urlshortener.service

import com.tommy.urlshortener.cache.ManagedCache
import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.extension.HashAlgorithm
import com.tommy.urlshortener.extension.toHashedHex
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class UrlShortService(
    private val managedCache: ManagedCache,
    private val shortenKeyGenerator: ShortenKeyGenerator,
    private val shortUrlGenerator: ShortUrlGenerator,
    private val shortenUrlRepository: ShortenUrlRepository,
) {
    private val logger = KotlinLogging.logger { }

    @Transactional
    fun shorten(originUrl: String, hashedOriginUrl: String): ShortUrlResponse {
        logger.debug { "URL Shorten - originUrl: [$originUrl]" }

        val redisKey = "$REDIS_KEY_PREFIX$hashedOriginUrl"
        val cachedShortUrl = managedCache.get<String>(redisKey)

        return cachedShortUrl?.let {
            ShortUrlResponse(it)
        } ?: run {
            val shortenUrl = shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl) ?: saveShortenUrl(originUrl)
            val shortUrl = shortenUrl.shortUrl

            managedCache.set(redisKey, shortUrl, 3L, TimeUnit.DAYS)

            ShortUrlResponse(shortUrl)
        }
    }

    private fun saveShortenUrl(originUrl: String): ShortenUrl {
        val timestamp = Instant.now().epochSecond

        val hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256)

        val shortenKey = shortenKeyGenerator.generate(timestamp)
        val generatedShortUrl = shortUrlGenerator.generate(shortenKey)

        return shortenUrlRepository.save(
            ShortenUrl(shortenKey = shortenKey, originUrl = originUrl, hashedOriginUrl = hashedOriginUrl, shortUrl = generatedShortUrl)
        )
    }

    companion object {
        private const val REDIS_KEY_PREFIX = "url:"
    }
}
