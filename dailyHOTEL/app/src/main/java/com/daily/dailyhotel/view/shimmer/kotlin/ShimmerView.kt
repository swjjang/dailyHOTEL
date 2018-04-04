package com.daily.dailyhotel.view.shimmer.kotlin

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class ShimmerView : View, ShimmerViewInterface {
    private var shimmerViewHelper: ShimmerViewHelper? = null

    override var gradientX: Float
        get() = shimmerViewHelper!!.gradientX
        set(gradientX) {
            shimmerViewHelper!!.gradientX = gradientX
        }

    override var isShimmering: Boolean
        get() = shimmerViewHelper!!.isShimmering
        set(isShimmering) {
            shimmerViewHelper!!.isShimmering = isShimmering
        }

    override val isSetUp: Boolean
        get() = shimmerViewHelper!!.isSetUp

    override var animationSetupCallback: ShimmerViewHelper.AnimationSetupCallback?
        get() = shimmerViewHelper!!.animationSetupCallback
        set(callback) {
            shimmerViewHelper!!.animationSetupCallback = callback
        }

    override var primaryColor: Int
        get() = shimmerViewHelper!!.primaryColor
        set(primaryColor) {
            shimmerViewHelper!!.primaryColor = primaryColor
        }

    override var reflectionColor: Int
        get() = shimmerViewHelper!!.reflectionColor
        set(reflectionColor) {
            shimmerViewHelper!!.reflectionColor = reflectionColor
        }

    constructor(context: Context) : super(context) {
        shimmerViewHelper = ShimmerViewHelper(this, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        shimmerViewHelper = ShimmerViewHelper(this, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        shimmerViewHelper = ShimmerViewHelper(this, attrs)
    }

    override fun setLinearGradientWidth(width: Float) {
        shimmerViewHelper!!.setLinearGradientWidth(width)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        if (shimmerViewHelper != null) {
            shimmerViewHelper!!.primaryColor = color
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (shimmerViewHelper != null) {
            shimmerViewHelper!!.onSizeChanged()
        }
    }

    public override fun onDraw(canvas: Canvas) {
        if (shimmerViewHelper != null) {
            shimmerViewHelper!!.onDraw(canvas)
        }
        super.onDraw(canvas)
    }
}

