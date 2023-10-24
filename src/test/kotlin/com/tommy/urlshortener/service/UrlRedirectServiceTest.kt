package com.tommy.urlshortener.service

import com.tommy.urlshortener.cache.ManagedCache
import com.tommy.urlshortener.extension.StringUtil
import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.exception.NotFoundException
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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class UrlRedirectServiceTest(
    @MockK private val managedCache: ManagedCache,
    @MockK private val shortenUrlRepository: ShortenUrlRepository,
) {
    @InjectMockKs
    private lateinit var sut: UrlRedirectService

    @Test
    @DisplayName("입력받은 short url로 origin url을 찾는다.")
    fun `find origin url`() {
        // Arrange
        val originUrl = UUID.randomUUID().toString()
        val shortUrl = "EysI9lHD"
        val shortenUrl = ShortenUrl(shortenKey = 1696691294L, originUrl = originUrl, hashedOriginUrl = StringUtil.hashToHex(originUrl), shortUrl = shortUrl)

        val redisKey = "$REDIS_KEY_PREFIX$shortUrl"

        every { managedCache.get<String>(redisKey) } returns null
        every { shortenUrlRepository.findByShortUrl(shortUrl) } returns shortenUrl
        justRun { managedCache.set(redisKey, shortenUrl.originUrl, 3L, TimeUnit.DAYS) }

        // Act
        val actual = sut.findOriginUrl(shortUrl)

        // Assert
        assertThat(actual.originUrl).isEqualTo(shortenUrl.originUrl)

        verify {
            managedCache.get<String>(redisKey)
            shortenUrlRepository.findByShortUrl(shortUrl)
            managedCache.set(redisKey, shortenUrl.originUrl, 3L, TimeUnit.DAYS)
        }
    }

    @Test
    @DisplayName("잘못된 short url로 origin url을 찾을 경우 NotFoundException이 발생한다.")
    fun `not found origin url`() {
        // Arrange
        val shortUrl = UUID.randomUUID().toString()
        val redisKey = "$REDIS_KEY_PREFIX$shortUrl"

        every { managedCache.get<String>(redisKey) } returns null
        every { shortenUrlRepository.findByShortUrl(shortUrl) } returns null

        // Act & Assert
        assertThrows<NotFoundException> { sut.findOriginUrl(shortUrl) }
    }

    companion object {
        private const val REDIS_KEY_PREFIX = "redirect:"
    }
}
