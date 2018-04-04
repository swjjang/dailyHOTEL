package com.daily.dailyhotel.view.shimmer.kotlin

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.view.View

class Shimmer {

    internal var repeatCount: Int = 0
    internal var duration: Long = 0
    internal var startDelay: Long = 0
    internal var direction: Int = 0
    internal var simmerWidth: Int = 0
    internal var linearGradientWidth: Float = 0.toFloat()
    internal var animatorListener: Animator.AnimatorListener? = null

    internal var animator: ObjectAnimator? = null

    val isAnimating: Boolean
        get() = animator != null && animator!!.isRunning

    init {
        repeatCount = DEFAULT_REPEAT_COUNT
        duration = DEFAULT_DURATION
        startDelay = DEFAULT_START_DELAY
        direction = DEFAULT_DIRECTION
        simmerWidth = DEFAULT_SIMMER_WIDTH
        linearGradientWidth = DEFAULT_LINEAR_GRADIENT_WIDTH.toFloat()
    }

    fun getRepeatCount(): Int {
        return repeatCount
    }

    fun setRepeatCount(repeatCount: Int): Shimmer {
        this.repeatCount = repeatCount
        return this
    }

    fun getDuration(): Long {
        return duration
    }

    fun setDuration(duration: Long): Shimmer {
        this.duration = duration
        return this
    }

    fun getSimmerWidth(): Int {
        return simmerWidth
    }

    fun setSimmerWidth(width: Int): Shimmer {
        this.simmerWidth = width
        return this
    }

    fun getStartDelay(): Long {
        return startDelay
    }

    fun setStartDelay(startDelay: Long): Shimmer {
        this.startDelay = startDelay
        return this
    }

    fun getDirection(): Int {
        return direction
    }

    fun setDirection(direction: Int): Shimmer {

        if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
            throw IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL")
        }

        this.direction = direction
        return this
    }

    fun getAnimatorListener(): Animator.AnimatorListener? {
        return animatorListener
    }

    fun setAnimatorListener(animatorListener: Animator.AnimatorListener): Shimmer {
        this.animatorListener = animatorListener
        return this
    }

    fun getLinearGradientWidth(): Float {
        return linearGradientWidth
    }

    fun setLinearGradientWidth(linearGradientWidth: Float): Shimmer {
        this.linearGradientWidth = linearGradientWidth
        return this
    }

    fun <V : View> start(shimmerView: V) where V : ShimmerViewInterface {

        if (isAnimating) {
            return
        }

        val animate = Runnable {
            shimmerView.isShimmering = true

            var fromX = 0f
            var toX = (if (simmerWidth == DEFAULT_SIMMER_WIDTH) shimmerView.width else simmerWidth).toFloat()
            if (direction == ANIMATION_DIRECTION_RTL) {
                fromX = (if (simmerWidth == DEFAULT_SIMMER_WIDTH) shimmerView.width else simmerWidth).toFloat()
                toX = 0f
            }

            val gradientWidth = if (linearGradientWidth == DEFAULT_LINEAR_GRADIENT_WIDTH.toFloat()) shimmerView.width else linearGradientWidth
            shimmerView.setLinearGradientWidth(gradientWidth)

            animator = ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX, toX)
            animator!!.repeatCount = repeatCount
            animator!!.duration = duration
            animator!!.startDelay = startDelay
            animator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    shimmerView.isShimmering = false

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        shimmerView.postInvalidate()
                    } else {
                        shimmerView.postInvalidateOnAnimation()
                    }

                    animator = null
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })

            if (animatorListener != null) {
                animator!!.addListener(animatorListener)
            }

            animator!!.start()
        }

        if (!shimmerView.isSetUp) {
            shimmerView.animationSetupCallback = ShimmerViewHelper.AnimationSetupCallback { animate.run() }
        } else {
            animate.run()
        }
    }

    fun cancel() {
        if (animator != null) {
            animator!!.cancel()
        }
    }

    companion object {

        val ANIMATION_DIRECTION_LTR = 0
        val ANIMATION_DIRECTION_RTL = 1

        private val DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE
        private val DEFAULT_DURATION: Long = 1500
        private val DEFAULT_START_DELAY: Long = 0
        private val DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR
        private val DEFAULT_SIMMER_WIDTH = -1
        private val DEFAULT_LINEAR_GRADIENT_WIDTH = -1
    }
}


