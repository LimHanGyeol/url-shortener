package com.tommy.urlshortener.service

import com.tommy.urlshortener.common.RedisService
import com.tommy.urlshortener.dto.OriginUrlResponse
import com.tommy.urlshortener.exception.NotFoundException
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class UrlRedirectService(
    private val redisService: RedisService,
    private val shortenUrlRepository: ShortenUrlRepository,
) {
    private val logger = KotlinLogging.logger { }

    @Transactional
    fun findOriginUrl(shortUrl: String): OriginUrlResponse {
        logger.debug { "shortUrl: [$shortUrl]" }

        val redisKey = "$REDIS_KEY_PREFIX$shortUrl"
        val cachedOriginUrl = redisService.get<String>(redisKey)

        return cachedOriginUrl?.let {
            OriginUrlResponse(it)
        } ?: run {
            val shortenUrl = shortenUrlRepository.findByShortUrl(shortUrl) ?: throw NotFoundException(SHORTEN_URL_NOT_FOUND, shortUrl)
            val originUrl = shortenUrl.originUrl

            redisService.set(redisKey, originUrl, 3L, TimeUnit.DAYS)

            OriginUrlResponse(originUrl)
        }
    }

    companion object {
        private const val REDIS_KEY_PREFIX = "redirect:"

        private val SHORTEN_URL_NOT_FOUND = "shortenUrl.notFound" to "shortenUrl not found. shortUrl: {0}"
    }
}
