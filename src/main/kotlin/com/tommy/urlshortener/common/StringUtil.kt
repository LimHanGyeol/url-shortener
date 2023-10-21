package com.tommy.urlshortener.common

import java.security.MessageDigest

object StringUtil {

    private const val SHA_256 = "SHA-256"

    fun hashToHex(input: String, hashAlgorithm: String = SHA_256): String {
        val bytes = MessageDigest.getInstance(hashAlgorithm).digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
