package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.util.ScreenUtils
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

    fun setInformationVisible(visible: Boolean) {
        viewDataBinding.informationLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setInformation(information: List<String>?) {
        if (viewDataBinding.informationLayout.childCount > 0) {
            viewDataBinding.informationLayout.removeAllViews()
        }

        information.takeNotEmpty {
            it.filter { !it.isTextEmpty() }.forEach {
                viewDataBinding.informationLayout.addView(getContentBulletView(it), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun getContentBulletView(text: String): DailyTextView {
        return DailyTextView(context).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
            setLineSpacing(1.0f, 1.0f)
            compoundDrawablePadding = ScreenUtils.dpToPx(context, 10.0)
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_circle_b666666, 0, 0, 0)
            setPadding(0, ScreenUtils.dpToPx(context, 14.0), 0, 0)
        }
    }
}
