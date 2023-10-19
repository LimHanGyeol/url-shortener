package com.tommy.urlshortener.service

import com.tommy.urlshortener.exception.InternalServerException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ShortUrlGenerator {

    private val logger = KotlinLogging.logger { }

    fun generate(shortenKey: Long): String {
        if (shortenKey == 0L) {
            return BASE62_CHARSET[0].toString()
        }

        var key = shortenKey // expect value: 16963183899999. 14자: timestamp + sequential number 4 (0001 ~ 9999)
        val stringBuilder = StringBuilder()

        while (key > 0) {
            val moduloValue = key % BASE62
            stringBuilder.append(BASE62_CHARSET[moduloValue.toInt()])
            key /= BASE62
        }

        val generatedUrl = stringBuilder.reverse().toString()

        if (generatedUrl.length > URL_LENGTH_LIMIT) {
            logger.error { "generated url limit length exceed: ${generatedUrl.length}" }
            throw InternalServerException(URL_LENGTH_LIMIT_EXCEED, generatedUrl, generatedUrl.length)
        }

        return generatedUrl
    }

    companion object {
        private const val BASE62 = 62
        private const val URL_LENGTH_LIMIT = 8
        private val BASE62_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()

        private val URL_LENGTH_LIMIT_EXCEED = "shortUrl.length.limit.exceed" to "current short url: {0}, length: {1}"
    }
}
