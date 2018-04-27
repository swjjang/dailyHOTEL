package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetail
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

    fun setInformation(information: Array<StayDetail.RefundInformation>) {

    }


    fun getInformationView(information: StayDetail.DetailInformation): View {

        val viewDataBinding: DailyViewDetailDetailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_detail_information_data, this, false)


        return viewDataBinding.root
    }

    private fun getContentBulletView(text: String): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c323232))
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_ic_text_dot_black, 0, 0, 0)
        }
    }
}
