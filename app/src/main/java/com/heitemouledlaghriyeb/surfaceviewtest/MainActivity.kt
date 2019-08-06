package com.heitemouledlaghriyeb.surfaceviewtest

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.sqrt
import android.graphics.Paint.Align



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

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onResume() {
        super.onResume()
        setContentView(GameSurface(this))
    }
}

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
        mainThread = MainThread(holder, this)
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
        val playerWidth = dpToPx(context, PLAYER_WIDTH)
        val playerHeight = dpToPx(context, PLAYER_HEIGHT.toFloat())
        val enemyWidth = dpToPx(context, ENEMY_WIDTH)
        val enemyHeight = dpToPx(context, ENEMY_HEIGHT.toFloat())
        background = Background(Color.TRANSPARENT)
        scoreText = Score(Color.WHITE)
        ground = Ground(Color.WHITE, dpToPx(context, GROUND_HEIGHT))
        blockyDude = BlockyDude(randomColor(), dpToPx(context, 100f), dpToPx(context, 5f),
            playerWidth.toInt(), playerHeight.toInt(), ground).apply {
            onTapListener = {
                if (!isGameOver) {
                    (it as BlockyDude).color = randomColor()
                    if (currentState == State.Grounded)
                        currentState = State.AboutToMove
                }
            }
            collided = {
                isGameOver = true
                gameOver?.invoke()
            }

            surpasses = {
                score += 10
                scoreText.score = score
            }
            addTo(addedCharacters)
        }
        blockyEnemy = BlockyEnemy(Color.parseColor("#fe4042"), dpToPx(context, 700f), dpToPx(context, 10f),
            enemyWidth.toInt(), enemyHeight.toInt(), ground).apply {
            //onTapListener = blockyDude.onTapListener
            addTo(addedCharacters)
        }

        gameOver = { score = 0 }
        groundBackground = GroundBackground(Color.TRANSPARENT, dpToPx(context, GROUND_HEIGHT))
        overlay = Overlay()
        gameOverText = GameOverText("GAME OVER", Color.WHITE)
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

abstract class Thing(var color: Int) {

    protected var canvas: Canvas? = null
    protected var context: Context? = null

    abstract fun update()

    open fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        this.context = context
        canvas?.let {
            this.canvas = it
            paint.shader = null
            paint.typeface = Typeface.DEFAULT
            paint.clearShadowLayer()
        }
    }
}

class Background(color: Int): Thing(color) {
    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        canvas?.let {
            val linearGradient = LinearGradient(0f, 0f, it.width.toFloat(), it.height.toFloat(),
                Color.parseColor("#ff9a9e"), Color.parseColor("#fad0c4"), Shader.TileMode.REPEAT)
            paint.shader = linearGradient
            paint.isDither = true
            it.drawRect(RectF(0f, 0f, it.width.toFloat(), it.height.toFloat()), paint)
        }
    }
}

class Score(color: Int) : Thing(color) {

    private var r = Rect()
    var score: Int = 0

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        val text = score.toString()
        paint.color = color
        paint.textSize = spToPx(context, 18f)
        paint.isAntiAlias = true
        canvas?.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint.textAlign = Align.CENTER
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth - r.width() - r.left - dpToPx(context, 16f)
        val y = dpToPx(context, 32f)
        paint.setShadowLayer(5f, dpToPx(context, 2f), dpToPx(context, 2f),
            Color.argb(80, 0, 0, 0))
        paint.isDither = true
        canvas?.drawText(text, x, y, paint)
    }
}

class Overlay(color: Int = Color.BLACK) : Thing(color) {

    private var alpha = 0

    override fun update() {
        if (alpha < 80) alpha += 5
    }

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = Color.argb(alpha, 0, 0, 0)
        canvas?.drawPaint(paint)
    }
}

class GameOverText(val text: String, color: Int) : Thing(color) {

    private var r = Rect()

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = color
        paint.textSize = spToPx(context, 40f)
        paint.isAntiAlias = true
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas?.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint.textAlign = Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth / 2f - r.width() / 2f - r.left
        val y = cHeight / 2f + r.height() / 2f - r.bottom
        canvas?.drawText(text, x, y, paint)
    }
}

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

class GroundBackground(color: Int, val height: Float) : Thing(color) {

    override fun update() {}

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        canvas?.let {
            val linearGradient = LinearGradient(0f, it.height - height, it.width.toFloat(),it.height - height,
                Color.parseColor("#02aab0"), Color.parseColor("#00cdac"), Shader.TileMode.REPEAT)
            paint.shader = linearGradient
            paint.isDither = true
            it.drawRect(0f, it.height - height, it.width.toFloat(), it.height.toFloat(), paint)
        }
    }
}

open class Character(color: Int, open var x: Float, open var y: Float, open val width: Int,
                     open val height: Int, private val ground: Ground) : Thing(color) {

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

class BlockyDude(color: Int, override var x: Float, override var y: Float, override val width: Int,
                 override val height: Int, ground: Ground) : Character(color, x, y, width, height, ground) {

    var collided: (() -> (Unit))? = null
    var surpasses: (() -> (Unit))? = null
    companion object {
        var surpassed = false
    }

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = color
        paint.setShadowLayer(5f, dpToPx(context, 5f), dpToPx(context, 2f), Color.argb(80, 0, 0, 0))
        paint.isDither = true
        canvas?.drawRect(x, y, x + width, y + height, paint)
    }

    override fun update() {
        if (GameSurface.isGameOver) return
        var maxJumpHeight = 120f
        context?.let {
            maxJumpHeight = dpToPx(it, maxJumpHeight)
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
                        currentState = State.Rising
                    } else {
                        velocity += 3f
                        currentState = State.Falling
                        lastState = State.Rising
                    }
                } else {
                    velocity += 0.5f
                    currentState = State.Falling
                }
                y += 1 * velocity
                if (y + height >= groundHeight()) {
                    y = groundHeight() - height
                }
            }
        } else {
            velocity = 0f
            lastState = State.Falling
            currentState = State.Grounded
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

class BlockyEnemy(color: Int, override var x: Float, override var y: Float, override val width: Int,
                  override val height: Int, ground: Ground) : Character(color, x, y, width, height, ground) {

    override var velocity = 1f
    private var xx: Float = -width.toFloat()

    override fun draw(context: Context, paint: Paint, canvas: Canvas?) {
        super.draw(context, paint, canvas)
        paint.color = color
        if (xx <= -width.toFloat()) {
            xx = canvas?.width?.toFloat() ?: 0f
            BlockyDude.surpassed = false
        }
        x = xx
        paint.setShadowLayer(5f, dpToPx(context, 5f), dpToPx(context, 2f), Color.argb(80, 0, 0, 0))
        paint.isDither = true
        canvas?.drawRect(xx, y, xx + width, y + height, paint)
    }

    override fun update() {
        context?.let {
            if (!isGrounded()) {
                velocity += dpToPx(it, 0.5f)
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