package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.*

class Background(context: Context, paint: Paint, color: Int): Thing(context, paint, color) {

    override fun update() {}

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            val linearGradient = LinearGradient(0f, 0f, it.width.toFloat(), it.height.toFloat(),
                Color.parseColor("#ff9a9e"), Color.parseColor("#fad0c4"), Shader.TileMode.REPEAT)
            paint.shader = linearGradient
            paint.isDither = true
            it.drawRect(RectF(0f, 0f, it.width.toFloat(), it.height.toFloat()), paint)
        }
    }
}