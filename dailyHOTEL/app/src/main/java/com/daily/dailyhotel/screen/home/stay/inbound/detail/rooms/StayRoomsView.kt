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

                    val view = pagerSnapHelper.findSnapView(viewDataBinding.recyclerView.layoutManager);
                    eventListener.onScrolled(viewDataBinding.recyclerView.getChildAdapterPosition(view), true)
                }
            })

            guideLayout.setOnClickListener(this@StayRoomsView)
            guideLayout.visibility = View.GONE
        }
    }

    override fun setToolbarTitle(title: String?) {
    }

    override fun onClick(v: View?) {

    }

    override fun setRoomList(roomList: MutableList<Room>, position: Int) {
        if (roomList.size == 0) {
            return
        }


    }

    override fun setGuideVisible(visible: Boolean) {
        viewDataBinding.guideLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }
}