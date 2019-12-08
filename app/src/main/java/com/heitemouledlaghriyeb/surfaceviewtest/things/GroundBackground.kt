package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.*

class GroundBackground(context: Context, paint: Paint, color: Int, val height: Float) : Thing(context, paint, color) {

    override fun update() {}

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            val linearGradient = LinearGradient(0f, it.height - height, it.width.toFloat(),it.height - height,
                Color.parseColor("#02aab0"), Color.parseColor("#00cdac"), Shader.TileMode.REPEAT)
            paint.shader = linearGradient
            paint.isDither = true
            it.drawRect(0f, it.height - height, it.width.toFloat(), it.height.toFloat(), paint)
        }
    }
}