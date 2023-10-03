package com.tommy.urlshortener.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class ShortUrlGeneratorTest {

    @Test
    @DisplayName("BASE62를 이용하여 ShortUrl을 생성한다.")
    fun `generate short url`() { // Arrange
        val shortenKey = 16963217089999L
        val sut = ShortUrlGenerator()

        // Act
        val actual = sut.generate(shortenKey)

        // Assert
        assertThat(actual).isEqualTo("EyoG2LrJ")
        assertThat(actual).hasSizeLessThanOrEqualTo(8)
        assertThat(actual).containsPattern("^[a-zA-Z0-9]+$")
    }
}
