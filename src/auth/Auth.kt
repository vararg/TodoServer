package com.vararg.auth

import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val CRYPT_ALGO = "HmacSHA1"

@KtorExperimentalAPI
val hashKey = hex(System.getenv("SECRET_KEY"))

@KtorExperimentalAPI
val hmacKey = SecretKeySpec(hashKey, CRYPT_ALGO)

@KtorExperimentalAPI
fun hash(password: String): String {
    val hmac = Mac.getInstance(CRYPT_ALGO)
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}
