package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
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

        fun onRoomFilterClick()
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
        viewDataBinding.roomFilterTextView.setOnClickListener { listener?.onRoomFilterClick() }
    }

    fun setRoomFilterListener(listener: OnDailyDetailRoomFilterListener) {
        this.listener = listener
    }

    fun setCalendar(text: CharSequence) {
        viewDataBinding.calendarTextView.text = text
    }

    fun setRoomFilterCount(count: Int) {
        viewDataBinding.roomFilterTextView.text = getRoomFilterCountText(count)
    }

    private fun getRoomFilterCountText(count: Int): CharSequence {
        return if (count == 0) {
            context.getString(R.string.label_stay_detail_room_filter)
        } else {
            val text = String.format(Locale.KOREA, "%s %d", context.getString(R.string.label_stay_detail_room_filter), count)
            val startIndex = text.lastIndexOf(' ')
            val endIndex = text.length

            SpannableStringBuilder(text).apply {
                setSpan(ForegroundColorSpan(resources.getColor(R.color.default_text_ceb2135)), //
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun setRoomFilterVisible(visible: Boolean) {
        viewDataBinding.roomFilterTextView.visibility = if (visible) View.GONE else View.VISIBLE
    }
}
