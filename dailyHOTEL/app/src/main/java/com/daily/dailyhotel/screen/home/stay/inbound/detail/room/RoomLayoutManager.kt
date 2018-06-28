package com.daily.dailyhotel.screen.home.stay.inbound.detail.room

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class RoomLayoutManager : LinearLayoutManager {

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

    private var scrollEnabled = true

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        super.smoothScrollToPosition(recyclerView, state, position)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)

        scrollHorizontallyBy(0, recycler, state)
    }

    fun setScrollEnabled(flag: Boolean) {
        this.scrollEnabled = flag
    }

    override fun canScrollHorizontally(): Boolean {
        return scrollEnabled && super.canScrollHorizontally()
    }

    override fun canScrollVertically(): Boolean {
        return scrollEnabled && super.canScrollVertically()
    }
}