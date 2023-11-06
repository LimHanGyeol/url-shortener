package com.tommy.urlshortener

import com.tommy.urlshortener.config.EmbeddedRedisConfig
import com.tommy.urlshortener.config.TestMockConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(EmbeddedRedisConfig::class, TestMockConfig::class)
@SpringBootTest
class UrlShortenerApplicationTests {

    @Test
    fun contextLoads() {
    }
}
