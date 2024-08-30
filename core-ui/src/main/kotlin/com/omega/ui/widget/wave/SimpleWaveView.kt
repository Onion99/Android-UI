package com.omega.ui.widget.wave

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.ColorUtils
import androidx.core.view.postDelayed
import kotlin.math.min

class SimpleWaveView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs,defStyleAttr){

    private var center = Point()
    private var radius = 0
    private var waveColor = 0xe9302d
    private var paint = Paint().apply {
        isAntiAlias = true
        color = waveColor
    }
    private val animDuration = 9999L
    var isAnimPause = false

    // ------------------------------------------------------------------------
    // prepare data
    // ------------------------------------------------------------------------
    private var waveDataList = ArrayList<SimpleWaveViewData>()
    init {
        waveDataList.add(SimpleWaveViewData(0f))
        waveDataList.add(SimpleWaveViewData(0f))
        waveDataList.add(SimpleWaveViewData(0f))
        waveDataList.forEachIndexed { index, simpleWaveViewData ->
            postDelayed((animDuration/3)*index){
                simpleWaveViewData.start()
            }
        }
    }

    // ------------------------------------------------------------------------
    // location
    // ------------------------------------------------------------------------
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        center.set(MeasureSpec.getSize(widthMeasureSpec)/2,MeasureSpec.getSize(heightMeasureSpec)/2)
        radius = min(MeasureSpec.getSize(widthMeasureSpec)/2,MeasureSpec.getSize(heightMeasureSpec)/2)
    }

    // ------------------------------------------------------------------------
    // draw
    // ------------------------------------------------------------------------
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(!isAnimPause){
            waveDataList.forEachIndexed { index, simpleWaveViewData ->
                val alpha = ((255 * (1f - simpleWaveViewData.currentAnimProgress)) * 0.4).toInt()
                // paint.setColor(Color.argb(alpha, 233, 50, 50))
                paint.setColor(ColorUtils.setAlphaComponent(waveColor,alpha))
                canvas.drawCircle(center.x.toFloat(), center.y.toFloat(),radius * simpleWaveViewData.currentAnimProgress,paint)
            }
            // ---- 使之进入无限的draw循环 ------
            invalidate()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveDataList.forEach {
            it.clear()
        }
        waveDataList.clear()
    }

    inner class SimpleWaveViewData(private var initRadiusPercent: Float) {
        private lateinit var valueAnimator: ValueAnimator
        var currentAnimProgress: Float = 0f

        fun start() {
            valueAnimator = ValueAnimator.ofFloat(initRadiusPercent, 1f)
            valueAnimator.setDuration(animDuration)
            valueAnimator.repeatMode = ValueAnimator.RESTART
            valueAnimator.repeatCount = ValueAnimator.INFINITE
            valueAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                currentAnimProgress = value
            }
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.start()
        }

        fun clear() {
            if (::valueAnimator.isInitialized) {
                valueAnimator.removeAllListeners()
                valueAnimator.removeAllUpdateListeners()
                valueAnimator.cancel()
            }
        }
    }
}