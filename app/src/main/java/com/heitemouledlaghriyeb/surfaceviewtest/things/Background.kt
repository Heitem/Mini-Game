package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.*

class Background(color: Int): Thing(color) {

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        canvas?.let {
            val linearGradient = LinearGradient(0f, 0f, it.width.toFloat(), it.height.toFloat(),
                Color.parseColor("#ff9a9e"), Color.parseColor("#fad0c4"), Shader.TileMode.REPEAT)
            paint.shader = linearGradient
            paint.isDither = true
            it.drawRect(RectF(0f, 0f, it.width.toFloat(), it.height.toFloat()), paint)
        }
    }
}