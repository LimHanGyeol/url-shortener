package com.tommy.urlshortener.service

import com.tommy.urlshortener.common.RedisService
import com.tommy.urlshortener.common.StringUtil
import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class UrlShortServiceTest(
    @MockK private val redisService: RedisService,
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
        val shortUrlRequest = ShortUrlRequest(originUrl)

        val redisKey = "$REDIS_KEY_PREFIX$originUrl"

        val shortenKey = 1696691294L
        val generatedShortUrl = "EysI9lHD"
        val shortenUrl = ShortenUrl(shortenKey = shortenKey, originUrl = originUrl, hashedOriginUrl = StringUtil.hashToHex(originUrl), shortUrl = generatedShortUrl)

        every { redisService.get<String>(redisKey) } returns null
        every { shortenUrlRepository.findByOriginUrl(originUrl) } returns null
        every { shortenKeyGenerator.generate(any()) } returns shortenKey
        every { shortUrlGenerator.generate(shortenKey) } returns generatedShortUrl
        every { shortenUrlRepository.save(any()) } returns shortenUrl
        justRun { redisService.set(redisKey, shortenUrl.shortUrl, 3L, TimeUnit.DAYS) }

        // Act
        val actual = sut.shorten(shortUrlRequest)

        // Assert
        assertThat(actual.shortUrl).isEqualTo(generatedShortUrl)
    }

    companion object {
        private const val REDIS_KEY_PREFIX = "url:"
    }
}
