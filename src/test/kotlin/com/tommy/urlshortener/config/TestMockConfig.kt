package com.tommy.urlshortener.config

import com.ninjasquad.springmockk.MockkBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@TestConfiguration
class TestMockConfig {

    @TestConfiguration
    class RedisMockConfig(
        @MockkBean private val redisConnectionFactory: RedisConnectionFactory,
        @MockkBean private val reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
    ) {
        @Bean
        fun redisTemplate(): RedisTemplate<String, Any> {
            val redisTemplate = RedisTemplate<String, Any>()
            redisTemplate.connectionFactory = redisConnectionFactory
            return redisTemplate
        }
    }
}
