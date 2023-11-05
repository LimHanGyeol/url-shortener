package com.tommy.urlshortener.service

import com.tommy.urlshortener.cache.ManagedCache
import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.extension.HashAlgorithm
import com.tommy.urlshortener.extension.toHashedHex
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class UrlShortServiceTest(
    @MockK private val managedCache: ManagedCache,
    @MockK private val shortenKeyGenerator: ShortenKeyGenerator,
    @MockK private val shortUrlGenerator: ShortUrlGenerator,
    @MockK private val shortenUrlRepository: ShortenUrlRepository,
) {
    @InjectMockKs
    private lateinit var sut: UrlShortService

    @Test
    @DisplayName("원본 URL을 단축한다.")
    fun `shorten origin url`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256)
        val redisKey = "$REDIS_KEY_PREFIX$hashedOriginUrl"

        val shortenKey = 1696691294L
        val generatedShortUrl = "EysI9lHD"
        val shortenUrl = ShortenUrl(shortenKey = shortenKey, originUrl = originUrl, hashedOriginUrl = hashedOriginUrl, shortUrl = generatedShortUrl)

        every { managedCache.get<String>(redisKey) } returns null
        every { shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl) } returns null
        every { shortenKeyGenerator.generate(any()) } returns shortenKey
        every { shortUrlGenerator.generate(shortenKey) } returns generatedShortUrl
        every { shortenUrlRepository.save(any()) } returns shortenUrl
        justRun { managedCache.set(redisKey, shortenUrl.shortUrl, 3L, TimeUnit.DAYS) }

        // Act
        val actual = sut.shorten(originUrl, hashedOriginUrl)

        // Assert
        assertThat(actual.shortUrl).isEqualTo(generatedShortUrl)

        verify {
            managedCache.get<String>(redisKey)
            shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl)
        }
    }

    companion object {
        private const val REDIS_KEY_PREFIX = "url:"
    }
}
