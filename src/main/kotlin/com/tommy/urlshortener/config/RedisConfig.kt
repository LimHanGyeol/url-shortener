package com.tommy.urlshortener.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: Int,
) {

    @Bean
    fun redissonClient(): RedissonClient { // 운영 환경에서의 Service Mode(Single, Cluster) 개선 필요
        val config = Config().apply {
            this.useSingleServer().address = "$REDISSON_HOST_PREFIX$host:$port"
        }
        return Redisson.create(config)
    }

    companion object {
        private const val REDISSON_HOST_PREFIX = "redis://" // SSL 통신 시 rediss:// 지정
    }
}
