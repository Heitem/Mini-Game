package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

abstract class Thing(var context: Context, var paint: Paint, var color: Int) {

    protected var canvas: Canvas? = null

    abstract fun update()

    open fun draw(canvas: Canvas?) {
        canvas?.let {
            this.canvas = it
            paint.shader = null
            paint.typeface = Typeface.DEFAULT
            paint.clearShadowLayer()
        }
    }
}