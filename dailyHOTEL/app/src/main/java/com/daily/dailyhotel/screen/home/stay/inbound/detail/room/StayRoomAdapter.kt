package com.daily.dailyhotel.screen.home.stay.inbound.detail.room

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.daily.base.util.*
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.Room
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.place.base.OnBaseEventListener

class StayRoomAdapter(private val context: Context, private val list: MutableList<Room>, private var nights: Int = 1) : RecyclerView.Adapter<StayRoomAdapter.RoomViewHolder>() {
    private var onEventListener: OnEventListener? = null

    interface OnEventListener : OnBaseEventListener {
        fun onMoreImageClick(position: Int)
        fun onVrImageClick(position: Int)
    }

    fun setEventListener(listener: OnEventListener?) {
        onEventListener = listener
    }

    fun getItem(position: Int): Room? {
        return if (position in 0 until list.size) {
            list[position]
        } else null
    }

    fun setData(list: MutableList<Room>) {
        this.list.clear()
        this.list += list
    }

    fun setNights(nights: Int) {
        this.nights = nights
    }

    fun getNights(): Int {
        return nights
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val roomLayout = StayRoomItemView(context)
        roomLayout.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
        roomLayout.setScale(StayRoomItemView.MIN_SCALE_VALUE)
        roomLayout.setBackgroundVisibile(true)

        return RoomViewHolder(roomLayout)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = getItem(position) ?: return

        val margin = getLayoutMargin()
        (holder.rootView.layoutParams as RecyclerView.LayoutParams).run {
            when (position) {
                0 -> {
                    leftMargin = margin.toInt()
                    rightMargin = 0
                }

                itemCount - 1 -> {
                    leftMargin = 0
                    rightMargin = margin.toInt()
                }

                else -> {
                    leftMargin = 0
                    rightMargin = 0
                }
            }
        }

        holder.rootView.setData(room, nights)
        holder.rootView.notifyDataSetChanged()
    }

    fun getLayoutMargin(): Float {
        return ScreenUtils.getScreenWidth(context) * (1.0f - StayRoomItemView.MIN_SCALE_VALUE) / 2.0f
    }

    inner class RoomViewHolder(val rootView: StayRoomItemView) : RecyclerView.ViewHolder(rootView)
}