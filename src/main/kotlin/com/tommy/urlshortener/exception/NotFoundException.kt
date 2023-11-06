package com.tommy.urlshortener.exception

import org.springframework.http.HttpStatus

class NotFoundException(pair: Pair<String, String>, vararg args: Any? = emptyArray()) : BaseException(HttpStatus.NOT_FOUND.value(), pair.first, pair.second)
