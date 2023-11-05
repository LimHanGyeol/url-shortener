package com.tommy.urlshortener.repository

import com.tommy.urlshortener.config.UrlShortenerConfig
import com.tommy.urlshortener.domain.ShortenUrl
import com.tommy.urlshortener.extension.HashAlgorithm
import com.tommy.urlshortener.extension.toHashedHex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ActiveProfiles("test")
@Import(UrlShortenerConfig::class)
@DataJpaTest
class ShortenUrlRepositoryTest @Autowired constructor(
    private val sut: ShortenUrlRepository,
) {

    @Test
    @DisplayName("originUrl이 주어질 경우 ShortenUrl Entity를 조회한다.")
    fun `find by origin url`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val shortenUrl = ShortenUrl(shortenKey = 1696691294L, originUrl = originUrl, hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256), shortUrl = "EysI9lHD")

        sut.save(shortenUrl)

        // Act
        val actual = sut.findByOriginUrl(originUrl)

        // Assert
        assertThat(actual?.originUrl).isEqualTo(shortenUrl.originUrl)
    }

    @Test
    @DisplayName("hashedOriginUrl이 주어질 경우 ShortenUrl Entity를 조회한다.")
    fun `find by hashed origin url`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256)

        val shortenUrl = ShortenUrl(shortenKey = 1696691294L, originUrl = originUrl, hashedOriginUrl = hashedOriginUrl, shortUrl = "EysI9lHD")

        sut.save(shortenUrl)

        // Act
        val actual = sut.findByHashedOriginUrl(hashedOriginUrl)

        // Assert
        assertThat(actual?.hashedOriginUrl).isEqualTo(shortenUrl.hashedOriginUrl)
    }

    @Test
    @DisplayName("shortUrl이 주어질 경우 ShortenUrl Entity를 조회한다.")
    fun `find by short url`() {
        // Arrange
        val originUrl = UUID.randomUUID().toString()
        val shortUrl = "EysI9lHD"
        val shortenUrl = ShortenUrl(shortenKey = 1696691294L, originUrl = originUrl, hashedOriginUrl = originUrl.toHashedHex(HashAlgorithm.SHA_256), shortUrl = shortUrl)

        sut.save(shortenUrl)

        // Act
        val actual = sut.findByShortUrl(shortUrl)

        // Assert
        assertThat(actual?.shortUrl).isEqualTo(shortenUrl.shortUrl)
    }
}
