package com.tommy.urlshortener.service

import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.extension.HashAlgorithm
import com.tommy.urlshortener.extension.toHashedHex
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UrlShortenFacadeTest(
    @MockK private val urlShortService: UrlShortService,
) {
    @InjectMockKs
    private lateinit var sut: UrlShortenFacade

    @Test
    @DisplayName("URL 단축 요청이 인입 될 경우 원본 Url을 단축하여 응답한다. 내부적으로 잠금이 실행된다.")
    fun `short url with lock`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val shortUrlRequest = ShortUrlRequest(originUrl)

        val hashedOriginUrl = shortUrlRequest.originUrl.toHashedHex(HashAlgorithm.SHA_256)
        val shortUrlResponse = ShortUrlResponse("EysI9lHD")

        every { urlShortService.shorten(shortUrlRequest.originUrl, hashedOriginUrl) } returns shortUrlResponse

        // Act
        val actual = sut.shortUrlWithLock(shortUrlRequest)

        // Assert
        assertThat(actual.shortUrl).isEqualTo(shortUrlResponse.shortUrl)

        verify { urlShortService.shorten(shortUrlRequest.originUrl, hashedOriginUrl) }
    }
}
