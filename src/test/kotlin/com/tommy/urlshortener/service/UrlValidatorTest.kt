package com.tommy.urlshortener.service

import com.tommy.urlshortener.exception.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UrlValidatorTest {

    private val sut = UrlValidator()

    @Test
    @DisplayName("올바른 URL일 경우 검증을 통과한다.")
    fun validate() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"

        // Act
        val actual = sut.validate(originUrl)

        // Assert
        assertThat(actual).isTrue
    }

    @Test
    @DisplayName("유효하지 않은 URL 형식일 경우 BadRequestException이 발생한다.")
    fun `validate failed`() {
        // Arrange
        val originUrl = "url-shortener, limhangyeol"

        // Act & Assert
        val actual = assertThrows<BadRequestException> { sut.validate(originUrl) }
        assertThat(actual.status).isEqualTo(400)
        assertThat(actual.code).isEqualTo("originUrl.invalid")
    }
}
