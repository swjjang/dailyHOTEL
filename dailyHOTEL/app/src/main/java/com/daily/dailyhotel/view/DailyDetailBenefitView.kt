package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBenefitDataBinding

class DailyDetailBenefitView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailBenefitDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_benefit_data, this, true)
    }

    fun setTitleText(title: String?) {
        viewDataBinding.benefitTitleTextView.text = title
    }

    fun setContents(contents: Array<String>?) {
        viewDataBinding.benefitContentsLayout.removeAllViews()

        contents?.filter { !it.isTextEmpty() }?.forEach {
            viewDataBinding.benefitContentsLayout.addView(getContentView(it), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun getContentView(text: String): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c323232))
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_xs, 0, 0, 0)
        }
    }

    fun setButtonEnabled(enabled: Boolean) {
        viewDataBinding.downloadCouponTextView.isEnabled = enabled
    }

    fun setButtonText(text: String) {
        viewDataBinding.downloadCouponTextView.text = text
    }
}
