package com.tommy.urlshortener.exception

open class BaseException(
    val status: Int,
    val code: String,
    val default: String = "",
    vararg val args: Any?,
) : RuntimeException()
