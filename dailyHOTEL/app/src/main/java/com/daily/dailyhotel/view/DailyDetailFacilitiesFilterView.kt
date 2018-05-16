package com.daily.dailyhotel.view

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.google.android.flexbox.FlexboxLayout
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailFacilitiesDataBinding

class DailyDetailFacilitiesFilterView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailFacilitiesDataBinding

    private var eventListener: OnEventListener? = null

    interface OnEventListener {
        fun onFacilitiesClick(facilities: String)
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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_facilities_filter_data, this, true)
    }

    fun setOnEventListener(listener: OnEventListener) {
        eventListener = listener
    }

    fun setFacilitiesList(facilitiesList: List<String>) {
        if (viewDataBinding.facilitiesGridLayout.childCount > 0) {
            viewDataBinding.facilitiesGridLayout.removeAllViews()
        }

        facilitiesList.forEach {
            viewDataBinding.facilitiesGridLayout.addView(createFacilitiesView(it))
        }
    }

    @SuppressLint("ResourceType")
    private fun createFacilitiesView(text: String): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)

            val dp12 = ScreenUtils.dpToPx(context, 12.0)
            val dp11 = ScreenUtils.dpToPx(context, 11.0)

            setPadding(dp12, dp11, dp12, dp11)
            setTextColor(context.resources.getColorStateList(R.drawable.selector_text_color_c929292_ceb2135))
            setBackgroundResource(R.drawable.selector_fillrect_ceb2135_ce6e6e7_r3)
            layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(context, 40.0)).apply {
                topMargin = ScreenUtils.dpToPx(context, 10.0)
            }

            setOnClickListener { eventListener?.onFacilitiesClick(text) }
        }
    }
}
