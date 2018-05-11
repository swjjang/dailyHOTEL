package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.FacilitiesPictogram
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailFacilitiesDataBinding

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
        viewDataBinding.roomCountTextView.text = context.getString(R.string.label_stay_detail_room_total_count, count)
    }

    fun setFacilities(facilities: List<FacilitiesPictogram>?) {
        viewDataBinding.facilitiesGridLayout.removeAllViews()

        facilities.takeNotEmpty {
            it.forEach {
                viewDataBinding.facilitiesGridLayout.addView(getFacilitiesView(it))

            }
        }
    }

    private fun getFacilitiesView(facilities: FacilitiesPictogram): View {
        return DailyTextView(context).apply {
            this.text = facilities.getName(context)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f)
            gravity = Gravity.CENTER_HORIZONTAL
            setTextColor(context.resources.getColor(R.color.default_text_c323232))
            setCompoundDrawablesWithIntrinsicBounds(0, facilities.getImageResourceId(), 0, 0)

            val layoutParams = android.support.v7.widget.GridLayout.LayoutParams()
            layoutParams.width = 0
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f)

            setLayoutParams(layoutParams)
        }
    }

}
