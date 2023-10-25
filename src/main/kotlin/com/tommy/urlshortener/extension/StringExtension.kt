package com.tommy.urlshortener.extension

import java.security.MessageDigest

fun String.toHashedHex(hashAlgorithm: HashAlgorithm = HashAlgorithm.SHA_256): String {
    val bytes = MessageDigest.getInstance(hashAlgorithm.label).digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

enum class HashAlgorithm(val label: String) {
    SHA_256("SHA-256"),
    ;
}
