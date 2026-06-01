package com.example.comparts.util

import kotlin.random.Random

object SkuGenerator {
    private val charPool: List<Char> = ('A'..'Z') + ('0'..'9')

    fun generateSku(prefix: String = "SKU"): String {
        // Varying length between 8 and 12 characters for the random part
        val length = Random.nextInt(8, 13)
        val randomString = (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        return "$prefix-$randomString"
    }
}
