package com.tommy.urlshortener.service

import com.tommy.urlshortener.exception.InternalServerException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class ShortUrlGeneratorTest {

    private val sut = ShortUrlGenerator()

    @Test
    @DisplayName("BASE62를 이용하여 ShortUrl을 생성한다.")
    fun `generate short url`() { // Arrange
        val shortenKey = 16963217089999L

        // Act
        val actual = sut.generate(shortenKey)

        // Assert
        assertThat(actual).isEqualTo("EyoG2LrJ")
        assertThat(actual).hasSizeLessThanOrEqualTo(8)
        assertThat(actual).containsPattern("^[a-zA-Z0-9]+$")
    }

    @Test
    @DisplayName("생성된 ShortUrl의 길이가 8자를 초과할 경우 InternalServerException이 발생한다.")
    fun `generated failed short url`() {
        // Arrange
        val shortenKey = 435457421885459952L // snowflake id. 2023-10-03 17:04:03.500

        // Act & Assert
        assertThrows<InternalServerException> { sut.generate(shortenKey) }
    }

    @Test
    @DisplayName("shortenKey의 값이 0일 경우 A로 단축한다.")
    fun `shorten key is zero`() {
        // Arrange
        val shortenKey = 0L

        // Act
        val actual = sut.generate(shortenKey)

        // Assert
        assertThat(actual).isEqualTo("A")
    }
}
