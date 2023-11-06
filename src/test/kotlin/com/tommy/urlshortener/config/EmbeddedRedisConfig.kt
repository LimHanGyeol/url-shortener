package com.tommy.urlshortener.config

import com.tommy.urlshortener.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.ActiveProfiles
import redis.embedded.RedisServer
import java.io.File
import java.io.IOException
import java.net.ServerSocket
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy

@ActiveProfiles("test")
@TestConfiguration
class EmbeddedRedisConfig(
    @Value("\${spring.data.redis.port:6379}")
    private val redisPort: Int,
) {

    private val logger = KotlinLogging.logger { }

    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        val port = if (isPortInUse(redisPort)) findAvailablePort() else redisPort

        redisServer = if (isArmArchitecture()) {
            val redisServerPath = File(REDIS_BINARY_PATH)
            RedisServer(redisServerPath, port)
        } else {
            RedisServer.builder()
                .port(redisPort)
                .setting(MAX_MEMORY)
                .build()
        }

        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    fun findAvailablePort(): Int {
        (10000..65535).forEach { port ->
            if (!isPortInUse(port)) {
                return port
            }
        }
        throw NotFoundException(PORT_NOT_FOUND)
    }

    private fun isPortInUse(port: Int): Boolean {
        return try {
            ServerSocket(port).close()
            false
        } catch (e: IOException) {
            logger.warn { "port in use. cannot be opened. Port: $port" }
            true
        }
    }

    private fun isArmArchitecture(): Boolean {
        val osArch = System.getProperty("os.arch").lowercase()
        println(osArch)
        return osArch.contains("aarch64") // arm
    }

    companion object {
        private const val MAX_MEMORY = "maxmemory 128M"
        private const val REDIS_BINARY_PATH = "src/test/resources/redis-server"
        private val PORT_NOT_FOUND = "redisPort.notFound" to "redis port not found."
    }
}
