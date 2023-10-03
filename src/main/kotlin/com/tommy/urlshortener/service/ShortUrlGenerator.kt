package com.tommy.urlshortener.service

import org.springframework.stereotype.Component

@Component
class ShortUrlGenerator {

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
            throw RuntimeException("단축된 URL이 8자 이상입니다. 관리자에게 문의해주세요.")
        }

        return generatedUrl
    }

    companion object {
        private const val BASE62 = 62
        private const val URL_LENGTH_LIMIT = 8
        private val BASE62_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    }
}
