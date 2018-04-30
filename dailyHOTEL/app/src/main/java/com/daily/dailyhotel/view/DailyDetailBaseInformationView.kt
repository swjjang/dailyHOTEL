package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBaseInformationDataBinding

class DailyDetailBaseInformationView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailBaseInformationDataBinding

    constructor(context: Context) : super(context) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initLayout(context)
    }

    private fun initLayout(context: Context) {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_base_information_data, this, true)
    }

    fun setCategoryName(text: String?) {
        viewDataBinding.categoryTextView.text = text
    }

    fun setRewardsVisible(visible: Boolean) {
        viewDataBinding.rewardTextGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setNameText(text: String?) {
        viewDataBinding.nameTextView.text = text
    }

    fun setAwardsVisible(visible: Boolean) {
        viewDataBinding.awardsGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
