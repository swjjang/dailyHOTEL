package com.daily.dailyhotel.view.shimmer.kotlin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.daily.base.util.ExLog
import com.twoheart.dailyhotel.R

class ShimmerView : View {

    private val paint: Paint = Paint()

    // center position of the gradient
    private var gradientX: Float = 0F
        set(gradientX) {
            field = gradientX
            invalidate()
        }

    // shader applied on the text view
    // only null until the first global layout
    private var linearGradient: LinearGradient? = null

    // shader's local matrix
    // never null
    private val linearGradientMatrix: Matrix = Matrix()

    private var primaryColor: Int = 0
        set(primaryColor) {
            field = primaryColor
            if (isSetUp) {
                resetLinearGradient()
            }
        }

    // shimmer reflection color
    private var reflectionColor: Int = 0
        set(reflectionColor) {
            field = reflectionColor
            if (isSetUp) {
                resetLinearGradient()
            }
        }

    // true when animating
    var isShimmering: Boolean = false

    // true after first global layout
    var isSetUp: Boolean = false
        private set

    // callback called after first global layout
    var callback: AnimationSetupCallback? = null

    var linearGradientWidth: Float = 0F
        set(linearGradientWidth) {
            field = linearGradientWidth
            if (isSetUp) {
                resetLinearGradient()
            }
        }

    constructor(context: Context) : super(context) {
        initialize(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        primaryColor = DEFAULT_PRIMARY_COLOR.toInt()
        reflectionColor = DEFAULT_REFLECTION_COLOR.toInt()

        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.shimmerView, 0, 0)?.let { a ->
                try {
                    primaryColor = a.getColor(R.styleable.shimmerView_primaryColor, DEFAULT_PRIMARY_COLOR.toInt())
                    reflectionColor = a.getColor(R.styleable.shimmerView_reflectionColor, DEFAULT_REFLECTION_COLOR.toInt())
                } catch (e: Exception) {
                    ExLog.e("sam : Error while creating the view:" + e.toString())
                } finally {
                    a.recycle()
                }
            }
        }
    }

    private fun resetLinearGradient() {
        // our gradient is a simple linear gradient from textColor to reflectionColor. its axis is at the center
        // when it's outside of the view, the outer color (textColor) will be repeated (Shader.TileMode.CLAMP)
        // initially, the linear gradient is positioned on the left side of the view
        val width = if (linearGradientWidth < 0) getWidth().toFloat() else linearGradientWidth
        linearGradient = LinearGradient(-width, 0f, 0f, 0f, intArrayOf(primaryColor, reflectionColor, primaryColor), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)

        paint.shader = linearGradient
    }


    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)

        primaryColor = color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        resetLinearGradient()

        if (!isSetUp) {
            isSetUp = true

            callback?.let { callback ->
                callback.onSetupAnimation(this)
            }
        }
    }

    /**
     * content of the wrapping view's onDraw(Canvas)
     * MUST BE CALLED BEFORE SUPER STATEMENT
     */
    override fun onDraw(canvas: Canvas) {
        // only draw the shader gradient over the text while animating
        if (isShimmering) {
            // first onDraw() when shimmering
            if (paint.shader == null) {
                paint.shader = linearGradient
            }

            // translate the shader local matrix
            linearGradientMatrix.setTranslate(2 * gradientX, 0f)

            // this is required in order to invalidate the shader's position
            linearGradient!!.setLocalMatrix(linearGradientMatrix)
            canvas.drawPaint(paint)
        } else {
            // we're not animating, remove the shader from the paint
            paint.shader = null
            paint.color = DEFAULT_PRIMARY_COLOR.toInt()
            canvas.drawPaint(paint)
        }

        super.onDraw(canvas)
    }

    companion object {
        private const val DEFAULT_REFLECTION_COLOR = 0xFFEBEBEB
        private const val DEFAULT_PRIMARY_COLOR = 0xFFF8F8F9
    }

    interface AnimationSetupCallback {
        fun onSetupAnimation(target: View)
    }

}