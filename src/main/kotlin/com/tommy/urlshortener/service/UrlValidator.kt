package com.tommy.urlshortener.service

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component

@Component
class UrlValidator {

    fun validate(originUrl: String) {
        if (!UrlValidator().isValid(originUrl)) {
            throw RuntimeException() // TODO: 올바르지 않은 URL 형식 Exception 발생
        }
    }
}
