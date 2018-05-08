package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailCheckTimeInformationDataBinding

class DailyDetailCheckTimeInformationView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailCheckTimeInformationDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_check_time_information_data, this, true)

    }

    fun setCheckTimeText(checkInTime: String?, checkOutTime: String?) {
        viewDataBinding.checkInTimeTextView.text = checkInTime
        viewDataBinding.checkOutTimeTextView.text = checkOutTime
    }

    fun setInformation(information: List<String>?) {
        viewDataBinding.informationLayout.removeAllViews()

        information.takeNotEmpty {
            it.filter { !it.isTextEmpty() }.forEach {
                viewDataBinding.informationLayout.addView(getInformationView(it), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun getInformationView(text: String): View {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c323232))
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_ic_text_dot_black, 0, 0, 0)
        }
    }
}
