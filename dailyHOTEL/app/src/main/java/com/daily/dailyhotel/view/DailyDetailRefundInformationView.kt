package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetailk
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailDetailInformationDataBinding

class DailyDetailRefundInformationView : LinearLayout {

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
        orientation = LinearLayout.VERTICAL
    }

    fun setInformation(information: StayDetailk.RefundInformation) {
        addView(getInformationView(information))
    }

    private fun getInformationView(information: StayDetailk.RefundInformation): View {
        val viewDataBinding: DailyViewDetailDetailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_detail_information_data, this, false)

        viewDataBinding.titleTextView.text = information.title

        information.contentList.takeNotEmpty {
            it.forEach {
                viewDataBinding.informationLayout.addView(getContentBulletView(it))
            }
        }

        viewDataBinding.moreTextView.visibility = View.GONE

        return viewDataBinding.root
    }

    private fun getContentBulletView(text: String): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
            setLineSpacing(1.0f, 1.0f)
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_circle_b666666, 0, 0, 0)
            setPadding(0, ScreenUtils.dpToPx(context, 14.0), 0, 0)
        }
    }
}
