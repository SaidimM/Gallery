package com.example.gallery.base.utils.blurHash

import android.graphics.Bitmap

/**
 * Utility methods to calculate blur hashes.
 */
object BlurHash {
    private fun applyBasisFunction(
        pixels: IntArray, width: Int, height: Int,
        normalisation: Double, i: Int, j: Int,
        factors: Array<DoubleArray>, index: Int
    ) {
        var r = 0.0
        var g = 0.0
        var b = 0.0
        for (x in 0 until width) {
            for (y in 0 until height) {
                val basis = (normalisation
                        * Math.cos(Math.PI * i * x / width)
                        * Math.cos(Math.PI * j * y / height))
                val pixel = pixels[y * width + x]
                r += basis * sRGBToLinear((pixel shr 16 and 0xff).toLong())
                g += basis * sRGBToLinear((pixel shr 8 and 0xff).toLong())
                b += basis * sRGBToLinear((pixel and 0xff).toLong())
            }
        }
        val scale = 1.0 / (width * height)
        factors[index][0] = r * scale
        factors[index][1] = g * scale
        factors[index][2] = b * scale
    }

    private fun encodeDC(value: DoubleArray): Long {
        val r = linearTosRGB(value[0])
        val g = linearTosRGB(value[1])
        val b = linearTosRGB(value[2])
        return (r shl 16) + (g shl 8) + b
    }

    private fun encodeAC(value: DoubleArray, maximumValue: Double): Long {
        val quantR = Math.floor(
            Math.max(
                0.0, Math.min(
                    18.0, Math.floor(
                        signPow(
                            value[0] / maximumValue, 0.5
                        ) * 9 + 9.5
                    )
                )
            )
        )
        val quantG = Math.floor(
            Math.max(
                0.0, Math.min(
                    18.0, Math.floor(
                        signPow(
                            value[1] / maximumValue, 0.5
                        ) * 9 + 9.5
                    )
                )
            )
        )
        val quantB = Math.floor(
            Math.max(
                0.0, Math.min(
                    18.0, Math.floor(
                        signPow(
                            value[2] / maximumValue, 0.5
                        ) * 9 + 9.5
                    )
                )
            )
        )
        return Math.round(quantR * 19 * 19 + quantG * 19 + quantB)
    }

    /**
     * Calculates the blur hash from the given image with 4x4 components.
     *
     * @param Bitmap the image
     * @return the blur hash
     */
    fun encode(Bitmap: Bitmap): String {
        return encode(Bitmap, 4, 4)
    }

    /**
     * Calculates the blur hash from the given image.
     * @param bitmap the image
     * @param componentX number of components in the x dimension
     * @param componentY number of components in the y dimension
     * @return the blur hash
     */
    fun encode(bitmap: Bitmap, componentX: Int, componentY: Int): String {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return encode(pixels, width, height, componentX, componentY)
    }

    /**
     * Calculates the blur hash from the given pixels.
     *
     * @param pixels width * height pixels, encoded as RGB integers (0xAARRGGBB)
     * @param width width of the bitmap
     * @param height height of the bitmap
     * @param componentX number of components in the x dimension
     * @param componentY number of components in the y dimension
     * @return the blur hash
     */
    fun encode(pixels: IntArray, width: Int, height: Int, componentX: Int, componentY: Int): String {
        require(!(componentX < 1 || componentX > 9 || componentY < 1 || componentY > 9)) { "Blur hash must have between 1 and 9 components" }
        require(width * height == pixels.size) { "Width and height must match the pixels array" }
        val factors = Array(componentX * componentY) { DoubleArray(3) }
        for (j in 0 until componentY) {
            for (i in 0 until componentX) {
                val normalisation: Double = if (i == 0 && j == 0) 1.0 else 2.toDouble()
                applyBasisFunction(
                    pixels, width, height,
                    normalisation, i, j,
                    factors, j * componentX + i
                )
            }
        }
        val hash = CharArray(1 + 1 + 4 + 2 * (factors.size - 1)) // size flag + max AC + DC + 2 * AC components
        val sizeFlag = (componentX - 1 + (componentY - 1) * 9).toLong()
        Base83.encode(sizeFlag, 1, hash, 0)
        val maximumValue: Double
        if (factors.size > 1) {
            val actualMaximumValue = max(factors, 1, factors.size)
            val quantisedMaximumValue =
                Math.floor(Math.max(0.0, Math.min(82.0, Math.floor(actualMaximumValue * 166 - 0.5))))
            maximumValue = (quantisedMaximumValue + 1) / 166
            Base83.encode(Math.round(quantisedMaximumValue), 1, hash, 1)
        } else {
            maximumValue = 1.0
            Base83.encode(0, 1, hash, 1)
        }
        val dc = factors[0]
        Base83.encode(encodeDC(dc), 4, hash, 2)
        for (i in 1 until factors.size) {
            Base83.encode(encodeAC(factors[i], maximumValue), 2, hash, 6 + 2 * (i - 1))
        }
        return String(hash)
    }
}