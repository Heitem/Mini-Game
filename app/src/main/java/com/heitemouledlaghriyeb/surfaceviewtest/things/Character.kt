package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Paint
import android.view.MotionEvent
import com.heitemouledlaghriyeb.surfaceviewtest.State
import java.util.ArrayList

open class Character(context: Context, paint: Paint, color: Int, open var x: Float, open var y: Float, open val width: Int,
                     open val height: Int, private val ground: Ground
) : Thing(context, paint, color) {

    var onTapListener: ((Character) -> (Unit))? = null
    open var velocity = 0f
    var lastState: State? = null
    var currentState: State? = null

    override fun update() {}

    fun isGrounded(): Boolean {
        canvas?.let {
            return y + height >= it.height - ground.height
        }
        return false
    }

    fun groundHeight(): Float {
        canvas?.let {
            return it.height - ground.height
        }
        return 0f
    }

    fun click(event: MotionEvent, inside: Boolean = true) {
        if (!inside) {
            onTapListener?.invoke(this)
            return
        }
        if (event.x >= x && event.x <= x + width && event.y >= y && event.y <= y + height) {
            onTapListener?.invoke(this)
        }
    }

    fun isVisible(): Boolean {
        canvas?.let {
            if (x >= 0 && x + width <= it.width) {
                return true
            }
        }
        return false
    }

    fun addTo(characters: ArrayList<Character>) {
        characters.add(this)
    }
}