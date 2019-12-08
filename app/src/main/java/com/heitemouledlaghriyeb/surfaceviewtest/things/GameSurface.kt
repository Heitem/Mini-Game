package com.heitemouledlaghriyeb.surfaceviewtest.things

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.heitemouledlaghriyeb.surfaceviewtest.*
import java.util.ArrayList

class GameSurface @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    var gameOver: (() -> (Unit))? = null

    private var mainThread: MainThread
    private var blockyDude: BlockyDude? = null
    private var blockyEnemy: BlockyEnemy? = null
    private var overlay: Overlay? = null
    private var gameOverText: GameOverText? = null
    private lateinit var ground: Ground
    private lateinit var background: Background
    private lateinit var scoreText: Score
    private lateinit var groundBackground: GroundBackground
    private var paint = Paint()

    companion object {
        var addedCharacters: ArrayList<Character> = arrayListOf()
        var isGameOver = false
        var score = 0
    }

    init {
        holder.addCallback(this)
        mainThread =
            MainThread(holder, this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mainThread.running = true
        mainThread.start()
        startGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        println("Surface changed")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        var retry = true
        while (retry) {
            try {
                mainThread.running = false
                mainThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                continue
            }
            retry = false
        }
    }

    fun startGame() {
        isGameOver = false
        addedCharacters = arrayListOf()
        val playerWidth = dpToPx(
            context,
            PLAYER_WIDTH
        )
        val playerHeight = dpToPx(
            context,
            PLAYER_HEIGHT.toFloat()
        )
        val enemyWidth = dpToPx(
            context,
            ENEMY_WIDTH
        )
        val enemyHeight = dpToPx(
            context,
            ENEMY_HEIGHT.toFloat()
        )
        background =
            Background(Color.TRANSPARENT)
        scoreText =
            Score(Color.WHITE)
        ground = Ground(
            Color.WHITE,
            dpToPx(
                context,
                GROUND_HEIGHT
            )
        )
        blockyDude = BlockyDude(
            randomColor(),
            dpToPx(context, 100f),
            dpToPx(context, 5f),
            playerWidth.toInt(), playerHeight.toInt(), ground
        ).apply {
            onTapListener = {
                if (!isGameOver) {
                    (it as BlockyDude).color =
                        randomColor()
                    if (currentState == State.Grounded)
                        currentState =
                            State.AboutToMove
                }
            }
            collided = {
                isGameOver = true
                gameOver?.invoke()
            }

            surpasses = {
                score += 10
                scoreText.score =
                    score
            }
            addTo(addedCharacters)
        }
        blockyEnemy = BlockyEnemy(
            Color.parseColor("#fe4042"),
            dpToPx(context, 700f),
            dpToPx(context, 10f),
            enemyWidth.toInt(), enemyHeight.toInt(), ground
        ).apply {
            //onTapListener = blockyDude.onTapListener
            addTo(addedCharacters)
        }

        gameOver = { score = 0 }
        groundBackground =
            GroundBackground(
                Color.TRANSPARENT,
                dpToPx(
                    context,
                    GROUND_HEIGHT
                )
            )
        overlay = Overlay()
        gameOverText =
            GameOverText(
                "GAME OVER",
                Color.WHITE
            )
    }

    fun update() {
        blockyDude?.update()
        blockyEnemy?.update()
        if (isGameOver) overlay?.update()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        background.draw(context, paint, canvas)
        scoreText.draw(context, paint, canvas)
        ground.draw(context, paint, canvas)
        blockyDude?.draw(context, paint, canvas)
        blockyEnemy?.draw(context, paint, canvas)
        groundBackground.draw(context, paint, canvas)
        if (isGameOver) {
            overlay?.draw(context, paint, canvas)
            gameOverText?.draw(context, paint, canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                blockyDude?.click(event, false)
                blockyEnemy?.click(event)
            }
            if (isGameOver) startGame()
        }
        return super.onTouchEvent(event)
    }
}