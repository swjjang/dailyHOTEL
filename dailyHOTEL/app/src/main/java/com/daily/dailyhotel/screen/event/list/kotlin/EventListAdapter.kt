package com.daily.dailyhotel.screen.event.list.kotlin

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.util.ExLog
import com.daily.base.util.ScreenUtils
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ListRowEventDataBinding
import com.twoheart.dailyhotel.network.model.Event
import com.twoheart.dailyhotel.util.Util


class EventListAdapter(val context: Context, private val eventList: MutableList<Event>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onClickListener: View.OnClickListener? = null

    init {
        addAll(eventList)
    }

    fun addAll(list: MutableList<Event>?) {
        list?.let {
//            eventList!!.addAll(list)


            eventList?.let {subList ->
                subList.addAll(it)
            } ?: mutableListOf<Event>()

            notifyDataSetChanged()
            ExLog.d("sam : eventList size : " + eventList?.size)
        }
    }

    fun getItem(position: Int): Event? {
        return eventList?.get(position)
    }

    fun clear() {
        eventList?.clear()
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataBinding: ListRowEventDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.list_row_event_data, parent, false)
        return EventViewHolder(dataBinding, onClickListener)
    }

    override fun getItemCount(): Int {
        return eventList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var event: Event = getItem(position) as Event

        holder.itemView.rootView.tag = event

        val dataBinding: ListRowEventDataBinding = (holder as EventViewHolder).dataBinding
        dataBinding.eventImageView.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder)
        Util.requestImageResize(context, dataBinding.eventImageView, ScreenUtils.getResolutionImageUrl(context, event.defaultImageUrl, event.lowResolutionImageUrl))
    }

    inner class EventViewHolder(val dataBinding: ListRowEventDataBinding, var onClickListener: View.OnClickListener?) : RecyclerView.ViewHolder(dataBinding.root) {
        init {
            onClickListener?.let { dataBinding.root.setOnClickListener(it) }
        }
    }
}