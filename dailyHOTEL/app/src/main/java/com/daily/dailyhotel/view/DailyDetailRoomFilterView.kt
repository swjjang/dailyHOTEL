package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomFilterDataBinding
import java.util.*

class DailyDetailRoomFilterView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomFilterDataBinding

    private var listener: OnDailyDetailRoomFilterListener? = null

    interface OnDailyDetailRoomFilterListener {
        fun onCalendarClick()

        fun onBedTypeFilterClick()

        fun onFacilitiesFilterClick()
    }

    constructor(context: Context) : super(context) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context)
    }

    private fun initLayout(context: Context) {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_room_filter_data, this, true)

        viewDataBinding.calendarTextView.setOnClickListener { listener?.onCalendarClick() }
        viewDataBinding.bedTypeFilterTextView.setOnClickListener { listener?.onBedTypeFilterClick() }
        viewDataBinding.facilitiesTextView.setOnClickListener { listener?.onFacilitiesFilterClick() }
    }

    fun setRoomFilterListener(listener: OnDailyDetailRoomFilterListener) {
        this.listener = listener
    }

    fun setCalendar(text: CharSequence) {
        viewDataBinding.calendarTextView.text = text
    }

    fun setBedTypeFilterCount(count: Int) {
        viewDataBinding.bedTypeFilterTextView.text = if (count > 0) String.format(Locale.KOREA, "%s %d", context.getString(R.string.frag_hotel_tab_bed_type), count)
        else context.getString(R.string.frag_hotel_tab_bed_type)
    }

    fun setFacilitiesFilterCount(count: Int) {
        viewDataBinding.facilitiesTextView.text = if (count > 0) String.format(Locale.KOREA, "%s %d", context.getString(R.string.label_room_amenities), count)
        else context.getString(R.string.label_room_amenities)
    }

    fun setSoldOutVisible(visible: Boolean) {
        val flag = if (visible) View.GONE else View.VISIBLE

        viewDataBinding.bedTypeFilterTextView.visibility = flag
        viewDataBinding.facilitiesTextView.visibility = flag
        viewDataBinding.rightGradientView.visibility = flag
    }
}
