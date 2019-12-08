package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import com.heitemouledlaghriyeb.surfaceviewtest.spToPx

class GameOverText(val text: String, color: Int) : Thing(color) {

    private var r = Rect()

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = color
        paint.textSize =
            spToPx(context, 40f)
        paint.isAntiAlias = true
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas?.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth / 2f - r.width() / 2f - r.left
        val y = cHeight / 2f + r.height() / 2f - r.bottom
        canvas?.drawText(text, x, y, paint)
    }
}