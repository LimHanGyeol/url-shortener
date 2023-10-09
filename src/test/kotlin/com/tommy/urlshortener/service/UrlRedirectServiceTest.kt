package com.tommy.urlshortener.service

import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.repository.ShortenUrlRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class UrlRedirectServiceTest(
    @MockK private val shortenUrlRepository: ShortenUrlRepository,
) {
    @InjectMockKs
    private lateinit var sut: UrlRedirectService

    @Test
    @DisplayName("입력받은 short url로 origin url을 찾는다.")
    fun `find origin url`() {
        // Arrange
        val shortUrl = "EysI9lHD"
        val shortenUrl = ShortenUrl(shortenKey = 1696691294L, originUrl = UUID.randomUUID().toString(), shortUrl = shortUrl)

        every { shortenUrlRepository.findByShortUrl(shortUrl) } returns shortenUrl

        // Act
        val actual = sut.findOriginUrl(shortUrl)

        // Assert
        assertThat(actual.originUrl).isEqualTo(shortenUrl.originUrl)
    }
}
