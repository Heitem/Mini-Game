package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Overlay(context: Context, paint: Paint, color: Int = Color.BLACK) : Thing(context, paint, color) {

    private var alpha = 0

    override fun update() {
        if (alpha < 80) alpha += 5
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = Color.argb(alpha, 0, 0, 0)
        canvas?.drawPaint(paint)
    }
}