package com.heitemouledlaghriyeb.surfaceviewtest

import android.graphics.Canvas
import android.view.SurfaceHolder

class MainThread(private var surfaceHolder: SurfaceHolder, private var gameSurface: GameSurface) : Thread() {

    var running: Boolean = false
    var averageFPS: Long = 0L

    companion object {
        var canvas: Canvas? = null
    }

    init {
        sleep(2000L)
    }

    override fun run() {
        val targetFPS = 60
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        var totalTime: Long = 0
        var frameCount = 0
        val targetTime = 1000 / targetFPS

        while (running) {
            startTime = System.nanoTime()
            canvas = null

            try {
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    this.gameSurface.update()
                    this.gameSurface.draw(canvas)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis

            try {
                sleep(waitTime)
            } catch (e: IllegalArgumentException) {}

            totalTime += System.nanoTime() - startTime
            frameCount++
            if (frameCount == targetFPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000)
                frameCount = 0
                totalTime = 0
                println(averageFPS)
            }
        }
    }
}