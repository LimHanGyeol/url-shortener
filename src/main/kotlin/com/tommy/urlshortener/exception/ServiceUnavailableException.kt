package com.tommy.urlshortener.exception

import org.springframework.http.HttpStatus

class ServiceUnavailableException(pair: Pair<String, String>, vararg args: Any?) : BaseException(HttpStatus.SERVICE_UNAVAILABLE.value(), pair.first, pair.second, args)
