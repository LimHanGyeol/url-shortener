package com.tommy.urlshortener.dto

import jakarta.validation.constraints.NotBlank

data class ShortUrlRequest(
    @NotBlank(message = "URL을 입력해주세요.")
    val originUrl: String,
)
