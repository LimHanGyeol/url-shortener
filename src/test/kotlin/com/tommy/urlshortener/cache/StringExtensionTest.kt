package com.tommy.urlshortener.cache

import com.tommy.urlshortener.extension.HashAlgorithm
import com.tommy.urlshortener.extension.toHashedHex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class StringExtensionTest {

    @Test
    @DisplayName("특정 문자열을 SHA-256 알고리즘으로 해시한다.")
    fun hashToHex() {
        // Arrange
        val input = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"

        // Act
        val actual = input.toHashedHex(HashAlgorithm.SHA_256)

        // Assert
        assertThat(actual).isEqualTo("bf7ff9439eedb0e61b0c0cd8393f48fe359b39369debc6066e05a0cb470c4f8f")
    }
}
