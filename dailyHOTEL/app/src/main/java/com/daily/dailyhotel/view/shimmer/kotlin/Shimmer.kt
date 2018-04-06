@file:JvmName("Shimmer")

package com.daily.dailyhotel.view.shimmer.kotlin

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import com.daily.base.util.VersionUtils

private const val ANIMATION_DIRECTION_LTR = 0
private const val ANIMATION_DIRECTION_RTL = 1

private const val DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE
private const val DEFAULT_DURATION: Long = 1500
private const val DEFAULT_START_DELAY: Long = 0
private const val DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR
private const val DEFAULT_SIMMER_WIDTH = -1
private const val DEFAULT_LINEAR_GRADIENT_WIDTH: Float = -1f

class Shimmer {
    var shimmerWidth: Int = DEFAULT_SIMMER_WIDTH
    var linearGradientWidth: Float = DEFAULT_LINEAR_GRADIENT_WIDTH
    var direction: Int = DEFAULT_DIRECTION
    var repeatCount: Int = DEFAULT_REPEAT_COUNT
    var duration: Long = DEFAULT_DURATION
    var startDelay: Long = DEFAULT_START_DELAY

    private val animatorSet = AnimatorSet()
    private val animatorList = mutableListOf<ObjectAnimator>()

    fun add(shimmerView: ShimmerView) {
        val with = if (shimmerWidth == DEFAULT_SIMMER_WIDTH) shimmerView.width else shimmerWidth
        val fromX: Int
        val toX: Int

        if (direction == ANIMATION_DIRECTION_RTL) {
            fromX = with
            toX = 0
        } else {
            fromX = 0
            toX = with
        }

        shimmerView.linearGradientWidth = if (linearGradientWidth == DEFAULT_LINEAR_GRADIENT_WIDTH) shimmerView.width.toFloat() else linearGradientWidth

        val animator = ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX.toFloat(), toX.toFloat()).apply {
            repeatCount = this@Shimmer.repeatCount
            duration = this@Shimmer.duration
            startDelay = this@Shimmer.startDelay

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    shimmerView.isShimmering = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    shimmerView.isShimmering = false

                    if (VersionUtils.isOverAPI16()) shimmerView.postInvalidateOnAnimation() else shimmerView.postInvalidate()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }

        animatorList += animator
    }

    @Synchronized
    fun start() {
        if (animatorSet.isRunning) {
            return
        }

        if (isAllViewSetUp()) {
            animatorSet.playTogether(animatorList as List<Animator>)
            animatorSet.start()
        } else {
            animatorList.forEach {
                (it.target as ShimmerView).callback = object : ShimmerView.AnimationSetupCallback {
                    override fun onSetupAnimation(target: View) {
                        start()
                    }
                }
            }
        }
    }

    private fun isAllViewSetUp(): Boolean {
        return animatorList.takeWhile { !((it.target as ShimmerView).isSetUp) }.isEmpty()
    }

    fun cancel() {
        animatorSet.cancel()
        animatorList.clear()
    }
}