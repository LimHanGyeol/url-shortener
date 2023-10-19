package com.tommy.urlshortener.service

import com.tommy.urlshortener.common.RedisService
import com.tommy.urlshortener.exception.TooManyRequestException
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ShortenKeyGenerator(
    private val redisService: RedisService,
) {
    fun generate(timestamp: Long): Long { // TODO: 동시성 문제 검토 필요
        val sequentialNumber = incrementSequentialNumber()

        return "$timestamp${String.format("%04d", sequentialNumber)}".toLong()
    }

    private fun incrementSequentialNumber(): Int {
        val incrementedValue = redisService.increment(REDIS_KEY, 1L)

        if (incrementedValue > MAX_SEQUENTIAL) {
            throw TooManyRequestException(MAX_SEQUENTIAL_NUMBER_EXCEED, incrementedValue)
        }
        redisService.set(REDIS_KEY, incrementedValue, 5, TimeUnit.SECONDS)

        return incrementedValue
    }

    companion object {
        private const val REDIS_KEY = "shortener:sequential"
        private const val MAX_SEQUENTIAL = 9999

        private val MAX_SEQUENTIAL_NUMBER_EXCEED = "maxSequential.exceed" to "current sequential number: {0}"
    }
}
