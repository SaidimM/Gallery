package com.example.gallery.blurHash

internal object Base83 {
    val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%*+,-.:;=?@[]^_{|}~"
        .toCharArray()

    private fun indexOf(a: CharArray, key: Char): Int {
        for (i in a.indices) {
            if (a[i] == key) {
                return i
            }
        }
        return -1
    }

    fun encode(value: Long, length: Int): String {
        val buffer = CharArray(length)
        encode(value, length, buffer, 0)
        return String(buffer)
    }

    fun encode(value: Long, length: Int, buffer: CharArray, offset: Int) {
        var exp = 1
        var i = 1
        while (i <= length) {
            val digit = (value / exp % 83).toInt()
            buffer[offset + length - i] = ALPHABET[digit]
            i++
            exp *= 83
        }
    }

    fun decode(value: String): Int {
        var result = 0
        val chars = value.toCharArray()
        for (i in chars.indices) {
            result = result * 83 + indexOf(ALPHABET, chars[i])
        }
        return result
    }
}