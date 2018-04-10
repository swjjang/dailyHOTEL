package com.daily.dailyhotel.screen.copy.kotlin

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.daily.base.BaseDialogView
import com.daily.dailyhotel.screen.event.list.kotlin.EventListAdapter
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityEventListDataBinding
import com.twoheart.dailyhotel.network.model.Event
import com.twoheart.dailyhotel.util.EdgeEffectColor

class EventListView(activity: EventListActivity, listener: EventListInterface.OnEventListener)//
    : BaseDialogView<EventListInterface.OnEventListener, ActivityEventListDataBinding>(activity, listener), EventListInterface.ViewInterface {

    private lateinit var eventListAdapter: EventListAdapter

    override fun setContentView(viewDataBinding: ActivityEventListDataBinding) {
        initToolbar(viewDataBinding)

        viewDataBinding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        EdgeEffectColor.setEdgeGlowColor(
                viewDataBinding.recyclerView,
                context.resources.getColor(R.color.default_over_scroll_edge)
        )

        viewDataBinding.homeButtonView.setOnClickListener { eventListener.onHomeButtonClick() }

        eventListAdapter = EventListAdapter(context, mutableListOf())
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityEventListDataBinding) {
        viewDataBinding.toolbarView.setTitleText(R.string.actionbar_title_event_list_frag)
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
    }

    override fun onEventList(eventList: MutableList<Event>) {
        if (!::eventListAdapter.isInitialized)
        {
            eventListAdapter = EventListAdapter(context, mutableListOf())
        } else {
            eventListAdapter.clear()
        }

        eventListAdapter.onClickListener = View.OnClickListener {
            var event = it.tag as Event
            eventListener.onItemClick(event)
        }

        if (eventList.size == 0)
        {
            viewDataBinding.recyclerView.visibility = View.GONE
            viewDataBinding.emptyLayout.visibility = View.VISIBLE
        } else {
            viewDataBinding.recyclerView.visibility = View.VISIBLE
            viewDataBinding.emptyLayout.visibility = View.GONE

            eventListAdapter.addAll(eventList)
            viewDataBinding.recyclerView.adapter = eventListAdapter
            eventListAdapter.notifyDataSetChanged()
        }
    }
}