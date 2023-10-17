package com.tommy.urlshortener.dto

class ErrorResult(
    val code: String,
    val message: String,
    val errorFields: List<ErrorField> = emptyList(),
) {
    /**
     * spring.validation의 @Valid, bindingResult로 인해 발생하는 실패 케이스를 매핑한다.
     */
    data class ErrorField(
        val field: String?,
        val value: Any?,
        val reason: String?,
    )
}
