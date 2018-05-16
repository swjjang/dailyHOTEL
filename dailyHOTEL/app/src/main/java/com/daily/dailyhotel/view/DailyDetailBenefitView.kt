package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBenefitDataBinding
import java.text.DecimalFormat

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

    fun setTitleVisible(visible: Boolean) {
        viewDataBinding.benefitTitleTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setTitleText(title: String?) {
        viewDataBinding.benefitTitleTextView.text = title
    }

    fun setContents(contents: List<String>?) {
        if (viewDataBinding.benefitContentsLayout.childCount > 0) {
            viewDataBinding.benefitContentsLayout.removeAllViews()
        }

        contents.takeNotEmpty {
            it.filter { !it.isTextEmpty() }.forEach {
                viewDataBinding.benefitContentsLayout.addView(createContentView(it))
            }
        }
    }

    fun setContentsVisible(visible: Boolean) {
        viewDataBinding.benefitContentsLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun createContentView(text: String): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            compoundDrawablePadding = ScreenUtils.dpToPx(context, 10.0)
            setLineSpacing(ScreenUtils.dpToPx(context, 1.0).toFloat(), 1.0f)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ic_check_xs, 0, 0, 0)

            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = ScreenUtils.dpToPx(context, 12.0)
            }
        }
    }

    fun setBeneiftVisible(visible: Boolean) {
        viewDataBinding.benefitLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setCouponButtonEnabled(enabled: Boolean) {
        viewDataBinding.downloadCouponGroup.isEnabled = enabled
    }

    fun setCouponButtonVisible(visible: Boolean) {
        viewDataBinding.downloadCouponGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setCouponButtonText(price: Int) {
        viewDataBinding.downloadCouponTextView.text = context.getString(R.string.label_detail_download_coupon, DecimalFormat("###,##0").format(price))
    }
}
