@file:JvmName("Shimmer")

package com.daily.dailyhotel.view.shimmer.kotlin

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import com.daily.base.util.VersionUtils

class Shimmer {
    private var repeatCount: Int = DEFAULT_REPEAT_COUNT
    private var duration: Long = DEFAULT_DURATION
    private var startDelay: Long = DEFAULT_START_DELAY

    private var direction: Int = DEFAULT_DIRECTION
        set(direction) {
            if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
                throw IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL")
            }

            field = direction
        }

    private var shimmerWidth: Int = DEFAULT_SIMMER_WIDTH
    private var linearGradientWidth: Float = DEFAULT_LINEAR_GRADIENT_WIDTH

    private val animatorSet = AnimatorSet()
    private val animatorList = mutableListOf<ObjectAnimator>()

    fun add(shimmerView: ShimmerView) {

        shimmerView.let { view ->
            val fromX: Int
            val toX: Int

            if (direction == ANIMATION_DIRECTION_RTL) {
                fromX = if (shimmerWidth == DEFAULT_SIMMER_WIDTH) view.width else shimmerWidth
                toX = 0
            } else {
                fromX = 0
                toX = if (shimmerWidth == DEFAULT_SIMMER_WIDTH) view.width else shimmerWidth
            }

            view.linearGradientWidth = if (linearGradientWidth == DEFAULT_LINEAR_GRADIENT_WIDTH) view.width.toFloat() else linearGradientWidth

            var animator = ObjectAnimator.ofFloat(view, "gradientX", fromX.toFloat(), toX.toFloat()).apply {
                repeatCount = this@Shimmer.repeatCount
                duration = this@Shimmer.duration
                startDelay = this@Shimmer.startDelay

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        view.isShimmering = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        view.isShimmering = false

                        if (VersionUtils.isOverAPI16()) {
                            view.postInvalidateOnAnimation()
                        } else {
                            view.postInvalidate()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })
            }

            if (!animatorList.contains(animator)) {
                animatorList += animator
            }
        }
    }

    fun start() {
        if (isAnimating()) {
            return
        }

        val animate = Runnable {
            animatorSet.playTogether(animatorList as Collection<Animator>?)
            animatorSet.start()
        }

        if (!isAllViewSetUp()) {
            for (animator in animatorList) {
                (animator.target as ShimmerView).callback = object : ShimmerView.AnimationSetupCallback {
                    override fun onSetupAnimation(target: View) {
                        if (isAllViewSetUp()) {
                            animate.run()
                        }
                    }
                }
            }
        } else {
            animate.run()
        }
    }

    fun isAllViewSetUp(): Boolean {
        if (animatorList.isEmpty()) {
            return false
        }

        for (animator in animatorList) {
            var shimmerView = animator.target as ShimmerView
            if (!shimmerView.isSetUp) {
                return false
            }
        }

        return true
    }

    fun cancel() {
        animatorSet.let { animatorSet ->
            animatorSet.cancel()
        }
    }

    fun isAnimating(): Boolean {
        return animatorSet.isRunning
    }

    companion object {
        const val ANIMATION_DIRECTION_LTR = 0
        const val ANIMATION_DIRECTION_RTL = 1

        private const val DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE
        private const val DEFAULT_DURATION: Long = 1500
        private const val DEFAULT_START_DELAY: Long = 0
        private const val DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR
        private const val DEFAULT_SIMMER_WIDTH = -1
        private const val DEFAULT_LINEAR_GRADIENT_WIDTH: Float = -1f
    }
}