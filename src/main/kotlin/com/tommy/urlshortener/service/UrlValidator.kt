package com.tommy.urlshortener.service

import com.tommy.urlshortener.exception.BadRequestException
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component

@Component
class UrlValidator {

    fun validate(originUrl: String): Boolean {
        if (!UrlValidator().isValid(originUrl)) {
            throw BadRequestException(INVALID_ORIGIN_URL, originUrl)
        }
        return true
    }

    companion object {
        private val INVALID_ORIGIN_URL = "originUrl.invalid" to "invalid origin url: {0}"
    }
}
