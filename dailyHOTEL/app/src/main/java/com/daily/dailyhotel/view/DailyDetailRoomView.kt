package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRoomDataBinding
import com.twoheart.dailyhotel.util.Util

class DailyDetailRoomView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailRoomDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_room_data, this, true)

        viewDataBinding.simpleDraweeView.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder)
    }

    fun setImageUrl(url: String?) {
        Util.requestImageResize(context, viewDataBinding.simpleDraweeView, url)
    }

    fun setRoomNameText(text: String?) {
        text.takeNotEmpty { viewDataBinding.roomNameTextView.text = it }
    }

    fun setRoomTypeText(text: String?) {
        text.takeNotEmpty { viewDataBinding.bedTypeTextView.text = it }
    }

    fun setBreakfastText(text: String?) {
        text.takeNotEmpty { viewDataBinding.breakfastTextView.text = it }
    }

    fun setAmenityText(text: String?) {
        text.takeNotEmpty { viewDataBinding.amenityTextView.text = it }
    }

    fun setPriceText(text: String?) {
        text.takeNotEmpty { viewDataBinding.discountPriceTextView.text = it }
    }

    fun setAvailableCouponVisible(visible: Boolean) {
        viewDataBinding.couponTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
