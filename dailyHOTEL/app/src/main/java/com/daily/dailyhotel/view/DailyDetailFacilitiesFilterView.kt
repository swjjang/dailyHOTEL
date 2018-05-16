package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailFacilitiesFilterDataBinding

class DailyDetailFacilitiesFilterView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailFacilitiesFilterDataBinding

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
}
