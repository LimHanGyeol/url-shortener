package com.tommy.urlshortener.common

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    @Value("\${spring.application.name:url-shortener}")
    private val appName: String,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: StringRedisTemplate,
) {
    private val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()

    fun set(key: String, value: Any, ttl: Long, timeUnit: TimeUnit) {
        val redisKey = generateKey(key)
        val serializedValue = objectMapper.writeValueAsString(value)
        valueOperations.set(redisKey, serializedValue, ttl, timeUnit)
    }

    fun <T> get(key: String): T? {
        val redisKey = generateKey(key)
        val value = valueOperations.get(redisKey)

        return value?.let {
            objectMapper.readValue(it, object : TypeReference<T>() {})
        }
    }

    fun increment(key: String, delta: Long = 1L): Int {
        val redisKey = generateKey(key)
        val incrementedValue = valueOperations.increment(redisKey, delta)
        return incrementedValue?.toInt() ?: 1
    }

    fun delete(key: String): Boolean = redisTemplate.delete(generateKey(key))

    fun hasKey(key: String): Boolean = redisTemplate.hasKey(generateKey(key))

    fun deleteAll(key: String) {
        val redisKey = generateKey(key)
        val keys = redisTemplate.keys("$redisKey:*")
        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
    }

    private fun generateKey(key: String): String {
        return "$appName:$key"
    }
}
