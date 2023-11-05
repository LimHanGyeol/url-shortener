package com.tommy.urlshortener.config

import com.ninjasquad.springmockk.MockkBean
import com.tommy.urlshortener.lock.DistributedLockAop
import io.mockk.impl.annotations.InjectMockKs
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory

@TestConfiguration
class TestMockConfig {

    @TestConfiguration
    class RedisMockConfig(
        @MockkBean private val redisConnectionFactory: RedisConnectionFactory,
        @MockkBean private val reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
    )

    @TestConfiguration
    class LockMockConfig(
        @MockkBean private val redissonClient: RedissonClient
    ) {
        @InjectMockKs
        private lateinit var distributedLockAop: DistributedLockAop
    }
}
