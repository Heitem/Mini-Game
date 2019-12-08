package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

abstract class Thing(var color: Int) {

    protected var canvas: Canvas? = null
    protected var context: Context? = null

    abstract fun update()

    open fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        this.context = context
        canvas?.let {
            this.canvas = it
            paint.shader = null
            paint.typeface = Typeface.DEFAULT
            paint.clearShadowLayer()
        }
    }
}