package more.tech.app.feature_main.presentation.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private var dataPoints: List<Int> = emptyList()
    private val barWidth = 40f
    private val barSpacing = 0f

    fun setDataPoints(dataPoints: List<Int>) {
        this.dataPoints = dataPoints
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val maxValue = dataPoints.maxOrNull() ?: 0
        val width = width.toFloat()
        val height = height.toFloat()
        val columnCount = dataPoints.size
        val totalWidth = columnCount * (barWidth + barSpacing) - barSpacing
        val startX = (width - totalWidth) / 2
        val maxHeight = height * 0.9f
        val normalizeFactor = maxHeight / maxValue

        paint.color = Color.BLUE

        for (i in dataPoints.indices) {
            val x = startX + i * (barWidth + barSpacing)
            val y = height - dataPoints[i] * normalizeFactor
            canvas.drawRect(x, y, x + barWidth, height, paint)
        }
    }
}