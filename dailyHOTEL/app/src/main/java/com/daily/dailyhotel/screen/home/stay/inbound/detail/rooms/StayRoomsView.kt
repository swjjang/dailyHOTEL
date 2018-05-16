package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.entity.Room
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityStayRoomsDataBinding
import com.twoheart.dailyhotel.util.EdgeEffectColor

class StayRoomsView(activity: StayRoomsActivity, listener: StayRoomsInterface.OnEventListener)//
    : BaseDialogView<StayRoomsInterface.OnEventListener, ActivityStayRoomsDataBinding>(activity, listener)
        , StayRoomsInterface.ViewInterface, View.OnClickListener {

    private lateinit var listAdapter: StayRoomAdapter

    override fun setContentView(viewDataBinding: ActivityStayRoomsDataBinding) {
        viewDataBinding.run {
            closeImageView.setOnClickListener({
                eventListener.onBackClick()
            })

            recyclerView.layoutManager = ZoomCenterLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge))

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(recyclerView)

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val view = pagerSnapHelper.findSnapView(viewDataBinding.recyclerView.layoutManager)
                    val position = viewDataBinding.recyclerView.getChildAdapterPosition(view)

                    setIndicatorText(position)
                    eventListener.onScrolled(position, true)
                }
            })

            if (!::listAdapter.isInitialized) {
                listAdapter = StayRoomAdapter(context, mutableListOf())
            }

            recyclerView.adapter = listAdapter

            guideLayout.setOnClickListener(this@StayRoomsView)
            guideLayout.visibility = View.GONE
        }
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.titleTextView.text = title
    }

    override fun setIndicatorText(position: Int) {
        val count = if (listAdapter.itemCount == 0) 1 else listAdapter.itemCount
        var pos = if (position < 0) 1 else if (position > count - 1) count - 1 else position + 1

        viewDataBinding.indicatorTextView.text = "$pos / ${if (count == 0) 1 else count}"
    }

    override fun onClick(v: View?) {

    }

    override fun setNights(nights: Int) {
        listAdapter.setNights(nights)
    }

    override fun setRoomList(roomList: MutableList<Room>, position: Int) {
        if (roomList.size == 0) {
            return
        }

        listAdapter.setData(roomList)

//        listAdapter.setOnEventListener(object : StayRoomAdapter.OnEventListener {
//            override fun onMoreImageClick(index: Int) {
//                eventListener.onMoreImageClick(index)
//            }
//
//            override fun onOderCountPlusClick(position: Int) {
//                eventListener.onMenuOderCountPlusClick(position)
//            }
//
//            override fun onOderCountMinusClick(position: Int) {
//                eventListener.onMenuOderCountMinusClick(position)
//            }
//
//            override fun onBackClick() {
//
//            }
//        })

        viewDataBinding.recyclerView.post {
            (viewDataBinding.recyclerView.layoutManager as LinearLayoutManager)//
                    .scrollToPositionWithOffset(position, listAdapter.getLayoutMargin().toInt())
        }
    }

    override fun setGuideVisible(visible: Boolean) {
        viewDataBinding.guideLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }
}