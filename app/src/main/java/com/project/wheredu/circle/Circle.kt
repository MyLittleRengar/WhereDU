package com.project.wheredu.circle

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import com.uravgcode.chooser.utils.ColorGenerator
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

open class Circle(var x: Float, var y: Float, radius: Float, var color: Int = ColorGenerator.nextColor()) {

    protected val corePaint = Paint().apply {
        color = this@Circle.color
        style = Paint.Style.FILL_AND_STROKE
    }

    protected val ringPaint = Paint().apply {
        color = this@Circle.color
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    protected val ringPaintLight = Paint().apply {
        color = Color.argb(65, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val center = RectF()
    private val ring = RectF()

    private var startAngle = Random.nextFloat() * 360
    private var sweepAngle = Random.nextFloat() * -360

    protected var coreRadius = 0f
    protected val defaultRadius = radius
    protected val radiusVariance = radius * 0.08f

    protected var winnerCircle = false
    protected var hasFinger = true

    protected var timeMillis = 0

    open fun update(deltaTime: Int) {
        val radius = coreRadius + radiusVariance * sin(timeMillis * 0.006).toFloat()
        val innerRadius = radius * 0.6f
        val strokeWidth = radius * 0.19f

        center.set(x - innerRadius, y - innerRadius, x + innerRadius, y + innerRadius)
        ring.set(x - radius, y - radius, x + radius, y + radius)

        corePaint.strokeWidth = strokeWidth
        ringPaint.strokeWidth = strokeWidth
        ringPaintLight.strokeWidth = strokeWidth

        startAngle = (startAngle + deltaTime * 0.3f) % 360
        if (sweepAngle <= 360) sweepAngle += deltaTime * 0.45f

        val target = if (hasFinger) defaultRadius else 0f
        coreRadius = if (coreRadius < target) {
            min(coreRadius + deltaTime * 0.6f, target)
        } else {
            max(coreRadius - deltaTime * 0.6f, target)
        }

        timeMillis += deltaTime
    }

    open fun draw(canvas: Canvas) {
        canvas.drawOval(center, corePaint)
        canvas.drawArc(ring, startAngle, sweepAngle, false, ringPaint)
        canvas.drawArc(center, startAngle + 180f, sweepAngle / 2f, false, ringPaintLight)
        canvas.drawArc(ring, startAngle, sweepAngle / 2f, false, ringPaintLight)
    }

    open fun removeFinger() {
        if (winnerCircle) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ hasFinger = false }, 1000)
        } else {
            hasFinger = false
        }
    }

    open fun setWinner() {
        winnerCircle = true
    }

    fun isWinner(): Boolean = winnerCircle

    fun getRadius(): Float = coreRadius

    fun isMarkedForDeletion(): Boolean = coreRadius <= 0
}