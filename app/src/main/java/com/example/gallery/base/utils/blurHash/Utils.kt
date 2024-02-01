package com.example.gallery.base.utils.blurHash

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.withSign


fun sRGBToLinear(value: Long): Double {
    val v = value / 255.0
    return if (v <= 0.04045) {
        v / 12.92
    } else {
        Math.pow((v + 0.055) / 1.055, 2.4)
    }
}

fun linearTosRGB(value: Double): Long {
    val v = Math.max(0.0, Math.min(1.0, value))
    return if (v <= 0.0031308) {
        (v * 12.92 * 255 + 0.5).toLong()
    } else {
        ((1.055 * Math.pow(v, 1 / 2.4) - 0.055) * 255 + 0.5).toLong()
    }
}

fun signPow(`val`: Double, exp: Double): Double {
    return abs(`val`).pow(exp).withSign(`val`)
}

fun max(values: Array<DoubleArray>, from: Int, endExclusive: Int): Double {
    var result = Double.NEGATIVE_INFINITY
    for (i in from until endExclusive) {
        for (j in 0 until values[i].size) {
            val value = values[i][j]
            if (value > result) {
                result = value
            }
        }
    }
    return result
}