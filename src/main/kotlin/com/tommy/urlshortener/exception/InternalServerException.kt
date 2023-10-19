package com.tommy.urlshortener.exception

import org.springframework.http.HttpStatus

class InternalServerException(pair: Pair<String, String>, vararg args: Any?) : BaseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), pair.first, pair.second)
