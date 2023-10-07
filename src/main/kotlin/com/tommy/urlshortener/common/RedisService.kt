package com.tommy.urlshortener.common

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    fun set(key: String, value: Any, duration: Long, timeUnit: TimeUnit) {
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(value::class.java)
        redisTemplate.opsForValue().set(key, value, duration, timeUnit)
    }

    fun get(key: String): Any? = redisTemplate.opsForValue().get(key)

    fun increment(key: String, delta: Long = 1L): Int {
        val incrementedValue = valueOperations.increment(key, delta)
        return incrementedValue?.toInt() ?: 1
    }

    fun increment(key: String, delta: Long = 1L, ttl: Long, timeUnit: TimeUnit): Int {
        val incrementedValue = valueOperations.increment(key, delta)
        if (incrementedValue == delta) {
            redisTemplate.expire(key, ttl, timeUnit)
        }
        return incrementedValue?.toInt() ?: 1
    }

    fun delete(key: String): Boolean = redisTemplate.delete(key)

    fun hasKey(key: String): Boolean = redisTemplate.hasKey(key)

    fun deleteAll(key: String) {
        val keys = redisTemplate.keys(key)
        redisTemplate.delete(keys)
    }
}
