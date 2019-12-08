package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.heitemouledlaghriyeb.surfaceviewtest.*

class BlockyDude(color: Int, override var x: Float, override var y: Float, override val width: Int,
                 override val height: Int, ground: Ground
) : Character(color, x, y, width, height, ground) {

    var collided: (() -> (Unit))? = null
    var surpasses: (() -> (Unit))? = null
    companion object {
        var surpassed = false
    }

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = color
        paint.setShadowLayer(5f,
            dpToPx(context, 5f),
            dpToPx(context, 2f), Color.argb(80, 0, 0, 0))
        paint.isDither = true
        canvas?.drawRect(x, y, x + width, y + height, paint)
    }

    override fun update() {
        if (GameSurface.isGameOver) return
        var maxJumpHeight = 120f
        context?.let {
            maxJumpHeight = dpToPx(
                it,
                maxJumpHeight
            )
        }
        val risingDecelerationHeight = (maxJumpHeight * 20) / 100
        val risingHeight = maxJumpHeight - risingDecelerationHeight
        if (lastState == null) {
            velocity = 1f
            lastState = State.Rising
        }
        if (!isGrounded() || currentState == State.AboutToMove) {
            if (lastState != State.Grounded) {
                if (lastState == State.Falling) {
                    if (y >= groundHeight() - risingHeight) {
                        velocity = -10f
                        currentState =
                            State.Rising
                    } else {
                        velocity += 3f
                        currentState =
                            State.Falling
                        lastState =
                            State.Rising
                    }
                } else {
                    velocity += 0.5f
                    currentState =
                        State.Falling
                }
                y += 1 * velocity
                if (y + height >= groundHeight()) {
                    y = groundHeight() - height
                }
            }
        } else {
            velocity = 0f
            lastState = State.Falling
            currentState =
                State.Grounded
        }

        if (collided() && !GameSurface.isGameOver) collided?.invoke()
        if (surpass() && !GameSurface.isGameOver) {
            if (!surpassed) {
                surpasses?.invoke()
                surpassed = true
            }
        }
    }

    private fun collided(): Boolean {
        for (thing in GameSurface.addedCharacters) {
            if (thing != this) {
                return (x + width >= thing.x && x < thing.x + thing.width && y + height >= thing.y)
            }
        }
        return false
    }

    private fun surpass(): Boolean {
        for (thing in GameSurface.addedCharacters) {
            if (thing != this) {
                return x >= thing.x + thing.width
            }
        }
        return false
    }
}