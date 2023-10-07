package com.tommy.urlshortener.service

import com.tommy.urlshortener.common.RedisService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class ShortenKeyGeneratorTest(
    @MockK private val redisService: RedisService,
) {
    @InjectMockKs
    private lateinit var sut: ShortenKeyGenerator

    @Test
    @DisplayName("URL 단축을 위한 Key를 생성한다.")
    fun `generate shorten key`() {
        // Arrange
        val timestamp = Instant.now().epochSecond

        every { redisService.increment(REDIS_KEY, 1L) } returns 300
        justRun { redisService.set(REDIS_KEY, 300, 5, TimeUnit.SECONDS) }

        // Act
        val actual = sut.generate(timestamp)

        // Assert
        assertThat(actual).isEqualTo("${timestamp}0300".toLong())
    }

    @Test
    @DisplayName("URL 단축을 위한 Sequential Number가 9999를 초과하면 실패한다.")
    fun `generate shorten key failed`() {
        // Arrange
        val timestamp = Instant.now().epochSecond

        every { redisService.increment(REDIS_KEY, 1L) } returns 10000
        justRun { redisService.set(REDIS_KEY, 10000, 5, TimeUnit.SECONDS) }

        // Act & Assert
        assertThrows<RuntimeException> { sut.generate(timestamp) }
    }

    companion object {
        private const val REDIS_KEY = "shortener:sequential"
    }
}
