package com.tommy.urlshortener.exception

class TooManyRequestException(pair: Pair<String, String>, vararg args: Any?) : BaseException(429, pair.first, pair.second, args)
