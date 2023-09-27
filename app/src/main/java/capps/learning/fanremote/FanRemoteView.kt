package capps.learning.fanremote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.min

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATION = -35

class FanRemoteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 0.0f
    private var fanSpeed = FanSpeed.OFF
    private val pointPosition = PointF(0.0f, 0.0f)

    private var lowColor = 0
    private var mediumColor = 0
    private var highColor = 0
    private var highestColor = 0

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.FanRemoteView) {
            lowColor = getColor(R.styleable.FanRemoteView_color1, 0)
            mediumColor = getColor(R.styleable.FanRemoteView_color2, 0)
            highColor = getColor(R.styleable.FanRemoteView_color3, 0)
            highestColor = getColor(R.styleable.FanRemoteView_color4, 0)
        }
    }

    fun changeLabelToWords(enable: Boolean) {
        if (enable) {
            fanSpeed.updateLabelsToWords()
        } else {
            fanSpeed.resetLabelToNumbers()
        }

        invalidate()
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(w, h) / 2.0 * 0.8).toFloat()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * kotlin.math.cos(angle)).toFloat() + width / 2
        y = (radius * kotlin.math.sin(angle)).toFloat() + height / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = when (fanSpeed) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> lowColor
            FanSpeed.MEDIUM -> mediumColor
            FanSpeed.HIGH -> highColor
            FanSpeed.HIGHEST -> highestColor
        }

        //Drawing the circle
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        //Drawing the indicator
        val markerRadius = radius + RADIUS_OFFSET_INDICATION
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

        //Drawing the label
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

    private enum class FanSpeed(var label: Int) {
        OFF(R.string.fan_off), LOW(R.string.fan_low), MEDIUM(R.string.fan_medium), HIGH(R.string.fan_high), HIGHEST(
            R.string.fan_highest
        );

        fun updateLabelsToWords() {
            values().forEach { fanSpeed ->
                fanSpeed.label = when (fanSpeed) {
                    OFF -> R.string.fan_off_word
                    LOW -> R.string.fan_low_word
                    MEDIUM -> R.string.fan_medium_word
                    HIGH -> R.string.fan_high_word
                    HIGHEST -> R.string.fan_highest_word
                }
            }
        }

        fun resetLabelToNumbers() {
            values().forEach { fanSpeed ->
                fanSpeed.label = when (fanSpeed) {
                    OFF -> R.string.fan_off
                    LOW -> R.string.fan_low
                    MEDIUM -> R.string.fan_medium
                    HIGH -> R.string.fan_high
                    HIGHEST -> R.string.fan_highest
                }
            }
        }


        fun next() = when (this) {
            OFF -> LOW
            LOW -> MEDIUM
            MEDIUM -> HIGH
            HIGH -> HIGHEST
            HIGHEST -> OFF
        }
    }
}