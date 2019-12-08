package com.heitemouledlaghriyeb.surfaceviewtest

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.heitemouledlaghriyeb.surfaceviewtest.things.GameSurface
import kotlin.math.sqrt


const val PLAYER_WIDTH = 20f
val PLAYER_HEIGHT = (1 + sqrt(5.0)) / 2 * PLAYER_WIDTH
const val ENEMY_WIDTH = 30f
val ENEMY_HEIGHT = (1 + sqrt(5.0)) / 2 * ENEMY_WIDTH
const val GROUND_HEIGHT = 40f

enum class State {
    Falling, Rising, Grounded, AboutToMove
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onResume() {
        super.onResume()
        setContentView(
            GameSurface(
                this
            )
        )
    }
}