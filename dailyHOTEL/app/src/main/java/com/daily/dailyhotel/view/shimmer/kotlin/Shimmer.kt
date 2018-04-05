@file:JvmName("Shimmer")

package com.daily.dailyhotel.view.shimmer.kotlin

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import com.daily.base.util.VersionUtils

class Shimmer {
    var repeatCount: Int = 0
    var duration: Long = 0
    var startDelay: Long = 0

    var direction: Int = 0
        set(direction) {
            if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
                throw IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL")
            }

            field = direction
        }

    var shimmerWidth: Int = 0
    var linearGradientWidth: Float = 0.toFloat()
    var animatorListener: Animator.AnimatorListener? = null

    var animator: ObjectAnimator? = null


    init {
        repeatCount = DEFAULT_REPEAT_COUNT
        duration = DEFAULT_DURATION
        startDelay = DEFAULT_START_DELAY
        direction = DEFAULT_DIRECTION
        shimmerWidth = DEFAULT_SIMMER_WIDTH
        linearGradientWidth = DEFAULT_LINEAR_GRADIENT_WIDTH.toFloat()
    }

    fun start(shimmerView: ShimmerView) {
        if (isAnimating()) {
            return
        }

        val animate: Runnable = Runnable {
            shimmerView.isShimmering = true

            var fromX = 0f
            var toX = (if (shimmerWidth == DEFAULT_SIMMER_WIDTH) shimmerView.width else shimmerWidth).toFloat()

            if (direction == ANIMATION_DIRECTION_RTL) {
                fromX = (if (shimmerWidth == DEFAULT_SIMMER_WIDTH) shimmerView.width else shimmerWidth).toFloat()
                toX = 0f
            }

            val gradientWidth = if (linearGradientWidth == DEFAULT_LINEAR_GRADIENT_WIDTH.toFloat()) shimmerView.width.toFloat() else linearGradientWidth
            shimmerView.linearGradientWidth = gradientWidth

            animator = ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX, toX)
            animator!!.repeatCount = repeatCount
            animator!!.duration = duration
            animator!!.startDelay = startDelay
            animator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    shimmerView.isShimmering = false

                    if (VersionUtils.isOverAPI16()) {
                        shimmerView.postInvalidateOnAnimation()
                    } else {
                        shimmerView.postInvalidate()
                    }

                    animator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })

            animatorListener?.let { listener ->
                animator!!.addListener(listener)
            }

            animator!!.start()
        }

        if (!shimmerView.isSetUp) {
            shimmerView.callback = object : ShimmerView.AnimationSetupCallback {
                override fun onSetupAnimation(target: View) {
                    animate.run()
                }
            }
        } else {
            animate.run()
        }
    }

    fun cancel() {
        animator?.let { animator ->
            animator.cancel()
        }
    }

    fun isAnimating(): Boolean {
        return animator != null && animator!!.isRunning;
    }

    companion object {
        const val ANIMATION_DIRECTION_LTR = 0
        const val ANIMATION_DIRECTION_RTL = 1

        private const val DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE
        private const val DEFAULT_DURATION: Long = 1500
        private const val DEFAULT_START_DELAY: Long = 0
        private const val DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR
        private const val DEFAULT_SIMMER_WIDTH = -1
        private const val DEFAULT_LINEAR_GRADIENT_WIDTH = -1
    }
}