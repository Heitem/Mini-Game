package com.heitemouledlaghriyeb.surfaceviewtest

import android.content.Context
import android.graphics.Color
import kotlin.math.roundToInt


fun getRandomInteger(min: Int, max: Int): Int {
    return ((Math.random() * ((max - min) + 1)) + min).roundToInt()
}

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

fun spToPx(context: Context, sp: Float): Float {
    return sp * context.resources.displayMetrics.scaledDensity
}

fun randomColor(): Int = Color.rgb(getRandomInteger(0, 255), getRandomInteger(0, 255), getRandomInteger(0, 255))