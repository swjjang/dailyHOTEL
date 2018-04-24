package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.twoheart.dailyhotel.R

class ZoomCenterLayoutManager  : LinearLayoutManager {

    constructor(context: Context) : super(context) {

        initialize(context)
    }

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {

        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

        initialize(context)
    }

    private fun initialize(context: Context) {}

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        val midpoint = width / 2f
        val d1 = DISTANCE * midpoint
        val s0 = 1f
        //            final float s1 = 1.f - AMOUNT;
        val childCount = childCount

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childMidpoint = (getDecoratedRight(childView) + getDecoratedLeft(childView)) / 2f
            val d = Math.min(d1, Math.abs(midpoint - childMidpoint))
            val scale = s0 - AMOUNT * d / d1
            val vectorValue = (1.0f - scale) / AMOUNT

            val blurView = childView.getTag(R.id.blurView) as View

            if (blurView != null) {
                blurView.alpha = vectorValue
            }
        }

        return scrolled
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        super.smoothScrollToPosition(recyclerView, state, position)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)

        scrollHorizontallyBy(0, recycler, state)
    }

    companion object {
        private val MIN_SCALE = 0.90f
        private val AMOUNT = 1.0f - MIN_SCALE // 1.0f - AMOUNT = MIN_SCALE
        private val DISTANCE = 0.75f
    }
}