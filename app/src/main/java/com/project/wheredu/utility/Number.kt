package com.project.wheredu.utility

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.sin

class Number(private var x: Float, private var y: Float, color: Int, private val number: Int, private var size: Float) {

    private val textPaint = Paint().apply {
        this.color = color
        textAlign = Paint.Align.CENTER
        textSize = size
    }

    private val xOrigin = x
    private var alpha = 255f
    private var alphaSpeed = 0f
    private var time = 0

    fun update(deltaTime: Int) {
        alphaSpeed += deltaTime * 0.00004f
        alpha -= alphaSpeed * deltaTime
        alphaSpeed += deltaTime * 0.00004f

        textPaint.alpha = alpha.coerceIn(0f, 255f).toInt()

        x = xOrigin + sin(time * 0.003).toFloat() * size * 0.25f
        y -= size * deltaTime * 0.0005f

        time += deltaTime
    }

    fun draw(canvas: Canvas) {
        canvas.drawText(number.toString(), x, y, textPaint)
    }

    fun isMarkedForDeletion(): Boolean = alpha <= 0
}