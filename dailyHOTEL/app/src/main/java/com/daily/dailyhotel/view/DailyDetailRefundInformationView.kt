package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRefundInformationDataBinding

class DailyDetailRefundInformationView : ConstraintLayout {

    private lateinit var viewDataBinding: DailyViewDetailRefundInformationDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_refund_information_data, this, true)

    }

    fun setInformation(information: StayDetail.RefundInformation?, hasNRDRoom: Boolean = false) {
        if (viewDataBinding.informationLayout.childCount > 0) {
            viewDataBinding.informationLayout.removeAllViews()
        }

        information?.let {
            it.contentList.takeNotEmpty {
                it.forEach {
                    viewDataBinding.informationLayout.addView(getContentBulletView(it), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }

            it.warningMessage.takeNotEmpty {
                viewDataBinding.informationLayout.addView(getContentBulletView(it, R.color.default_text_ceb2135), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun getContentBulletView(text: String, textColorResourceId: Int = R.color.default_text_c4d4d4d): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            setTextColor(context.resources.getColor(textColorResourceId))
            setLineSpacing(1.0f, 1.0f)
            compoundDrawablePadding = ScreenUtils.dpToPx(context, 10.0)
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_circle_b666666, 0, 0, 0)
            setPadding(0, ScreenUtils.dpToPx(context, 16.0), 0, 0)
        }
    }
}
