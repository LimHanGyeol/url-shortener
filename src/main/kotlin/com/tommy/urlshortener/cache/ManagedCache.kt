package com.tommy.urlshortener.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ManagedCache(
    @Value("\${spring.application.name:url-shortener}")
    private val appName: String,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: StringRedisTemplate,
) {
    private val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()

    fun set(key: String, value: Any, ttl: Long, timeUnit: TimeUnit) {
        val redisKey = generateKey(key)
        val serializedValue = objectMapper.writeValueAsString(CacheValue(value))
        valueOperations.set(redisKey, serializedValue, ttl, timeUnit)
    }

    /*
     ```
     Jackson Kotlin Extensions 에서 아래 확장 함수로 TypeReference를 주입하고 있다.
     CacheValue를 Wrapping하여 별도의 TypeReference 선언을 하지 않는다.
     inline fun <reified T> jacksonTypeRef(): TypeReference<T> = object: TypeReference<T>() {}
     inline fun <reified T> ObjectMapper.readValue(content: String): T = readValue(content, jacksonTypeRef<T>())
     ```
     */
    fun <T> get(key: String): T? {
        val redisKey = generateKey(key)
        val value = valueOperations.get(redisKey)

        return value?.let {
            val cacheValue: CacheValue<T> = objectMapper.readValue(it)
            cacheValue.value
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

    data class CacheValue<T>(val value: T)
}
