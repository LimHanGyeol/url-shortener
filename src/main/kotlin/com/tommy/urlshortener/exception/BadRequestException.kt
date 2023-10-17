package com.tommy.urlshortener.exception

class BadRequestException(pair: Pair<String, String>, vararg args: Any?) : BaseException(400, pair.first, pair.second, args)
