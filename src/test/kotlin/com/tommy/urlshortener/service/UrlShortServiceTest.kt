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
    @DisplayName("Cache에 값이 있을 경우 Cache의 short url을 반환한다.")
    fun `find short url by cache`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256)

        val redisKey = "$REDIS_KEY_PREFIX$hashedOriginUrl"

        val shortUrl = "EysI9lHD"

        every { managedCache.get<String>(redisKey) } returns shortUrl

        // Act
        val actual = sut.shorten(originUrl, hashedOriginUrl)

        // Assert
        assertThat(actual.shortUrl).isEqualTo(shortUrl)

        verify { managedCache.get<String>(redisKey) }
        verify(exactly = 0) {
            shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl)
            shortenKeyGenerator.generate(any())
            shortUrlGenerator.generate(any())
            shortenUrlRepository.save(any())
            managedCache.set(redisKey, any(), 3L, TimeUnit.DAYS)
        }
    }

    @Test
    @DisplayName("Cache에 값이 없을 경우 DB에 origin url에 해당하는 short url을 반환한다.")
    fun `find short url by database`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256)

        val redisKey = "$REDIS_KEY_PREFIX$hashedOriginUrl"

        val shortUrl = "EysI9lHD"
        val shortenUrl = ShortenUrl(shortenKey = 1696691294L, originUrl = originUrl, hashedOriginUrl = hashedOriginUrl, shortUrl = shortUrl)

        every { managedCache.get<String>(redisKey) } returns null
        every { shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl) } returns shortenUrl
        justRun { managedCache.set(redisKey, shortenUrl.shortUrl, 3L, TimeUnit.DAYS) }

        // Act
        val actual = sut.shorten(originUrl, hashedOriginUrl)

        // Assert
        assertThat(actual.shortUrl).isEqualTo(shortUrl)

        verify {
            managedCache.get<String>(redisKey)
            shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl)
            managedCache.set(redisKey, shortenUrl.shortUrl, 3L, TimeUnit.DAYS)
        }

        verify(exactly = 0) {
            shortenKeyGenerator.generate(any())
            shortUrlGenerator.generate(any())
            shortenUrlRepository.save(any())
        }
    }

    @Test
    @DisplayName("Cache에 값이 없고, DB에도 값이 없을 경우 origin url을 단축하여 반환한다.")
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
        every { shortenUrlRepository.save(shortenUrl) } returns shortenUrl
        justRun { managedCache.set(redisKey, shortenUrl.shortUrl, 3L, TimeUnit.DAYS) }

        // Act
        val actual = sut.shorten(originUrl, hashedOriginUrl)

        // Assert
        assertThat(actual.shortUrl).isEqualTo(generatedShortUrl)

        verify {
            managedCache.get<String>(redisKey)
            shortenUrlRepository.findByHashedOriginUrl(hashedOriginUrl)
            shortenKeyGenerator.generate(any())
            shortUrlGenerator.generate(shortenKey)
            shortenUrlRepository.save(shortenUrl)
            managedCache.set(redisKey, shortenUrl.shortUrl, 3L, TimeUnit.DAYS)
        }
    }

    companion object {
        private const val REDIS_KEY_PREFIX = "url:"
    }
}
