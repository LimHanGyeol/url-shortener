package com.tommy.urlshortener.service

import com.tommy.urlshortener.common.RedisService
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ShortenKeyGenerator(
    private val redisService: RedisService,
) {
    fun generate(timestamp: Long): String { // TODO: 동시성 문제 검토 필요
        val sequentialNumber = incrementSequentialNumber()

        return "$timestamp${String.format("%04d", sequentialNumber)}"
    }

    private fun incrementSequentialNumber(): Int {
        val incrementedValue = redisService.increment(REDIS_KEY, 1L)

        if (incrementedValue > MAX_SEQUENTIAL) {
            throw RuntimeException() // TODO: 초당 최대 생성수 초과 잠시후 재시도 권유
        }
        redisService.set(REDIS_KEY, incrementedValue, 5, TimeUnit.SECONDS)

        return incrementedValue
    }

    companion object {
        private const val REDIS_KEY = "shortener:sequential"
        private const val MAX_SEQUENTIAL = 9999
    }
}
