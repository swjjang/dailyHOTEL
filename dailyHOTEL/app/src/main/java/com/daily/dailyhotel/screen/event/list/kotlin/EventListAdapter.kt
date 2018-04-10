package com.daily.dailyhotel.screen.event.list.kotlin

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.util.ScreenUtils
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ListRowEventDataBinding
import com.twoheart.dailyhotel.network.model.Event
import com.twoheart.dailyhotel.util.Util

class EventListAdapter(private val context: Context, private val eventList: MutableList<Event>)
    : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    var onClickListener: View.OnClickListener? = null

    init {
        addAll(eventList)
    }

    fun addAll(list: MutableList<Event>) {
        eventList.addAll(list)
    }

    fun getItem(position: Int): Event? {
        return eventList[position]
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun clear() {
        eventList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val dataBinding: ListRowEventDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.list_row_event_data, parent, false)
        return EventViewHolder(dataBinding, onClickListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        var event = getItem(position) ?: return

        holder.itemView.rootView.tag = event

        holder.dataBinding.eventImageView.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder)
        Util.requestImageResize(context, holder.dataBinding.eventImageView
                , ScreenUtils.getResolutionImageUrl(context, event.defaultImageUrl, event.lowResolutionImageUrl))
    }

    inner class EventViewHolder(val dataBinding: ListRowEventDataBinding, var onClickListener: View.OnClickListener?)
        : RecyclerView.ViewHolder(dataBinding.root) {
        init {
            onClickListener?.let { dataBinding.root.setOnClickListener(it) }
        }
    }
}