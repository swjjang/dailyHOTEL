package com.daily.dailyhotel.view.shimmer.kotlin

import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

import com.daily.base.util.ExLog
import com.twoheart.dailyhotel.R

class ShimmerViewHelper(private val view: View, attributeSet: AttributeSet) {
    private val paint: Paint

    // center position of the gradient
    var gradientX: Float = 0.toFloat()
        set(gradientX) {
            field = gradientX
            view.invalidate()
        }

    // shader applied on the text view
    // only null until the first global layout
    private var linearGradient: LinearGradient? = null

    // shader's local matrix
    // never null
    private var linearGradientMatrix: Matrix? = null

    private var primaryColor: Int = 0

    // shimmer reflection color
    private var reflectionColor: Int = 0

    // true when animating
    var isShimmering: Boolean = false

    // true after first global layout
    var isSetUp: Boolean = false
        private set

    // callback called after first global layout
    var animationSetupCallback: AnimationSetupCallback? = null

    private var linearGradientWidth: Float = 0.toFloat()

    interface AnimationSetupCallback {
        fun onSetupAnimation(target: View)
    }

    init {
        this.paint = Paint()
        this.paint.isAntiAlias = true
        this.paint.style = Paint.Style.FILL

        init(attributeSet)
    }

    fun getPrimaryColor(): Int {
        return primaryColor
    }

    fun setPrimaryColor(primaryColor: Int) {
        this.primaryColor = primaryColor
        if (isSetUp) {
            resetLinearGradient()
        }
    }

    fun getReflectionColor(): Int {
        return reflectionColor
    }

    fun setReflectionColor(reflectionColor: Int) {
        this.reflectionColor = reflectionColor
        if (isSetUp) {
            resetLinearGradient()
        }
    }

    fun setLinearGradientWidth(width: Float) {
        this.linearGradientWidth = if (width < 0) view.width else width
        if (isSetUp) {
            resetLinearGradient()
        }
    }

    private fun init(attributeSet: AttributeSet?) {

        primaryColor = DEFAULT_PRIMARY_COLOR
        reflectionColor = DEFAULT_REFLECTION_COLOR

        if (attributeSet != null) {
            val a = view.context.obtainStyledAttributes(attributeSet, R.styleable.shimmerView, 0, 0)
            if (a != null) {
                try {
                    primaryColor = a.getColor(R.styleable.shimmerView_primaryColor, DEFAULT_PRIMARY_COLOR)
                    reflectionColor = a.getColor(R.styleable.shimmerView_reflectionColor, DEFAULT_REFLECTION_COLOR)
                } catch (e: Exception) {
                    ExLog.e("sam : Error while creating the view:" + e.toString())
                } finally {
                    a.recycle()
                }
            }
        }

        linearGradientMatrix = Matrix()
    }

    private fun resetLinearGradient() {
        // our gradient is a simple linear gradient from textColor to reflectionColor. its axis is at the center
        // when it's outside of the view, the outer color (textColor) will be repeated (Shader.TileMode.CLAMP)
        // initially, the linear gradient is positioned on the left side of the view
        val width = if (linearGradientWidth < 0) view.width else linearGradientWidth
        linearGradient = LinearGradient(-width, 0f, 0f, 0f, intArrayOf(primaryColor, reflectionColor, primaryColor), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)

        paint.shader = linearGradient
    }

    fun onSizeChanged() {
        resetLinearGradient()

        if (!isSetUp) {
            isSetUp = true

            if (animationSetupCallback != null) {
                animationSetupCallback!!.onSetupAnimation(view)
            }
        }
    }

    /**
     * content of the wrapping view's onDraw(Canvas)
     * MUST BE CALLED BEFORE SUPER STATEMENT
     */
    fun onDraw(canvas: Canvas) {
        // only draw the shader gradient over the text while animating
        if (isShimmering) {
            // first onDraw() when shimmering
            if (paint.shader == null) {
                paint.shader = linearGradient
            }

            // translate the shader local matrix
            linearGradientMatrix!!.setTranslate(2 * this.gradientX, 0f)

            // this is required in order to invalidate the shader's position
            linearGradient!!.setLocalMatrix(linearGradientMatrix)
            canvas.drawPaint(paint)
        } else {
            // we're not animating, remove the shader from the paint
            paint.shader = null
            paint.color = DEFAULT_PRIMARY_COLOR
            canvas.drawPaint(paint)
        }
    }

    companion object {

        private val DEFAULT_REFLECTION_COLOR = -0x141415
        private val DEFAULT_PRIMARY_COLOR = -0x70707
    }
}


