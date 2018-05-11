package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daily.dailyhotel.entity.Room
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ListRowStayRoomDataBinding

class StayRoomAdapter(private val context: Context, private val list: MutableList<Room>) : RecyclerView.Adapter<StayRoomAdapter.RoomViewHolder>() {

    fun getItem(position: Int): Room {
        return (list.size > 0).let { list[position] }
    }

    fun setData(list: MutableList<Room>) {
        this.list.clear()
        this.list += list
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val dataBinding = DataBindingUtil.inflate<ListRowStayRoomDataBinding>(
                LayoutInflater.from(parent.context), R.layout.list_row_stay_room_data, parent, false)
        return RoomViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val dataBinding : ListRowStayRoomDataBinding = holder.dataBinding



    }


    inner class RoomViewHolder(val dataBinding: ListRowStayRoomDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}