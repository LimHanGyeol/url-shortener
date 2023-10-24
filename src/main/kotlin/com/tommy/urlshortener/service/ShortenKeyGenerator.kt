package com.tommy.urlshortener.service

import com.tommy.urlshortener.cache.ManagedCache
import com.tommy.urlshortener.exception.TooManyRequestException
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ShortenKeyGenerator(
    private val managedCache: ManagedCache,
) {
    fun generate(timestamp: Long): Long {
        val sequentialNumber = incrementSequentialNumber()

        return "$timestamp${String.format("%04d", sequentialNumber)}".toLong()
    }

    private fun incrementSequentialNumber(): Int {
        val incrementedValue = managedCache.increment(REDIS_KEY, 1L)

        if (incrementedValue > MAX_SEQUENTIAL) {
            throw TooManyRequestException(MAX_SEQUENTIAL_NUMBER_EXCEED, incrementedValue)
        } else {
            managedCache.set(REDIS_KEY, incrementedValue, 5, TimeUnit.SECONDS)
        }

        return incrementedValue
    }

    companion object {
        private const val REDIS_KEY = "shortener:sequential"
        private const val MAX_SEQUENTIAL = 9999

        private val MAX_SEQUENTIAL_NUMBER_EXCEED = "maxSequential.exceed" to "current sequential number: {0}"
    }
}
