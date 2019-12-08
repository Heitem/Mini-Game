package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.heitemouledlaghriyeb.surfaceviewtest.dpToPx
import com.heitemouledlaghriyeb.surfaceviewtest.spToPx

class Score(color: Int) : Thing(color) {

    private var r = Rect()
    var score: Int = 0

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        val text = score.toString()
        paint.color = color
        paint.textSize =
            spToPx(context, 18f)
        paint.isAntiAlias = true
        canvas?.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint.textAlign = Paint.Align.CENTER
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth - r.width() - r.left - dpToPx(
            context,
            16f
        )
        val y = dpToPx(context, 32f)
        paint.setShadowLayer(5f,
            dpToPx(context, 2f),
            dpToPx(context, 2f),
            Color.argb(80, 0, 0, 0))
        paint.isDither = true
        canvas?.drawText(text, x, y, paint)
    }
}