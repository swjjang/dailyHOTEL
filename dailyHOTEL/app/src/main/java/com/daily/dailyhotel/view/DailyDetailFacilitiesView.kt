package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.FacilitiesPictogram
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailFacilitiesDataBinding
import java.text.DecimalFormat

class DailyDetailFacilitiesView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailFacilitiesDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_facilities_data, this, true)
    }

    fun setRoomCountVisible(visible: Boolean) {
        viewDataBinding.roomCountTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setRoomCount(count: Int) {
        viewDataBinding.roomCountTextView.text = context.getString(R.string.label_stay_detail_room_total_count, DecimalFormat("###,##0").format(count))
    }

    fun setFacilities(facilities: List<FacilitiesPictogram>?) {
        if (viewDataBinding.facilitiesGridLayout.childCount > 0) {
            viewDataBinding.facilitiesGridLayout.removeAllViews()
        }

        facilities.takeNotEmpty {
            it.forEach { viewDataBinding.facilitiesGridLayout.addView(getFacilitiesView(it)) }

            val columnCount = viewDataBinding.facilitiesGridLayout.columnCount - (it.size % viewDataBinding.facilitiesGridLayout.columnCount)

            if (viewDataBinding.facilitiesGridLayout.columnCount > columnCount) {
                for (i in 0 until columnCount) {
                    viewDataBinding.facilitiesGridLayout.addView(getFacilitiesEmptyView())
                }
            }
        }
    }

    private fun getFacilitiesView(facilities: FacilitiesPictogram): View {
        return DailyTextView(context).apply {
            this.text = facilities.getName(context)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f)
            gravity = Gravity.CENTER_HORIZONTAL
            setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
            setCompoundDrawablesWithIntrinsicBounds(0, facilities.getImageResourceId(), 0, 0)
            compoundDrawablePadding = ScreenUtils.dpToPx(context, 3.0)

            val layoutParams = android.support.v7.widget.GridLayout.LayoutParams()
            layoutParams.width = 0
            layoutParams.height = android.support.v7.widget.GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f)
            layoutParams.topMargin = ScreenUtils.dpToPx(context, 11.0)
            layoutParams.bottomMargin = ScreenUtils.dpToPx(context, 12.0)

            setLayoutParams(layoutParams)
        }
    }

    private fun getFacilitiesEmptyView(): View {
        return DailyTextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f)
            gravity = Gravity.CENTER_HORIZONTAL

            val layoutParams = android.support.v7.widget.GridLayout.LayoutParams()
            layoutParams.width = 0
            layoutParams.height = android.support.v7.widget.GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f)
            layoutParams.topMargin = ScreenUtils.dpToPx(context, 11.0)
            layoutParams.bottomMargin = ScreenUtils.dpToPx(context, 12.0)

            setLayoutParams(layoutParams)
        }
    }
}
