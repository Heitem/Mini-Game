package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

class Ground(color: Int, val height: Float) : Thing(color) {

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = color
        canvas?.drawLine(0f, canvas.height - height, canvas.width.toFloat(),
            canvas.height - height, paint
        )
    }
}