package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.heitemouledlaghriyeb.surfaceviewtest.dpToPx

class BlockyEnemy(context: Context, paint: Paint, color: Int, override var x: Float, override var y: Float, override val width: Int,
                  override val height: Int, ground: Ground
) : Character(context, paint, color, x, y, width, height, ground) {

    override var velocity = 1f
    private var xx: Float = -width.toFloat()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = color
        if (xx <= -width.toFloat()) {
            xx = canvas?.width?.toFloat() ?: 0f
            BlockyDude.surpassed = false
        }
        x = xx
        paint.setShadowLayer(5f,
            dpToPx(context, 5f),
            dpToPx(context, 2f), Color.argb(80, 0, 0, 0))
        paint.isDither = true
        canvas?.drawRect(xx, y, xx + width, y + height, paint)
    }

    override fun update() {
        context?.let {
            if (!isGrounded()) {
                velocity += dpToPx(
                    it,
                    0.5f
                )
                y += 1 * velocity
                if (y + height >= groundHeight()) {
                    y = groundHeight() - height
                }
            } else {
                if (!GameSurface.isGameOver)
                    xx -= dpToPx(it, 5f)
            }
        }
    }
}