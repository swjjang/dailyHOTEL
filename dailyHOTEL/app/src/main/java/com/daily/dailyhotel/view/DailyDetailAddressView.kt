package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailAddressDataBinding

class DailyDetailAddressView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailAddressDataBinding

    private var clickListener: OnAddressClickListener? = null

    interface OnAddressClickListener {
        fun onMapClick()

        fun onCopyAddressClick()

        fun onSearchAddressClick()
    }

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_address_data, this, true)

        viewDataBinding.mapIconImageView.setOnClickListener { clickListener?.onMapClick() }
        viewDataBinding.copyAddressTextView.setOnClickListener { clickListener?.onCopyAddressClick() }
        viewDataBinding.searchAddressTextView.setOnClickListener { clickListener?.onSearchAddressClick() }
    }

    fun setOnAddressClickListener(clickListener: OnAddressClickListener?) {
        this.clickListener = clickListener
    }

    fun setAddressText(text: String?) {
        text.takeNotEmpty { viewDataBinding.addressTextView.text = it }
    }
}
