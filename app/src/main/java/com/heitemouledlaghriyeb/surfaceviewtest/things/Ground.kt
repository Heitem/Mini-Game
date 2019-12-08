package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

class Ground(context: Context, paint: Paint, color: Int, val height: Float) : Thing(context, paint, color) {

    override fun update() {}

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = color
        canvas?.drawLine(0f, canvas.height - height, canvas.width.toFloat(),
            canvas.height - height, paint
        )
    }
}