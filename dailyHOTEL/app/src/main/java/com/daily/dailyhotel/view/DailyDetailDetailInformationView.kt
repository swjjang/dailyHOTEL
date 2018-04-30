package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.daily.base.util.FontManager
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetailk
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBreakfastInformationDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewDetailDetailInformationDataBinding
import com.twoheart.dailyhotel.databinding.LayoutStayDetailWaitforbookingDataBinding
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan

class DailyDetailDetailInformationView : LinearLayout {

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

    fun setInformation(information: Array<StayDetailk.DetailInformation>, waitBookingInformationVisible: Boolean) {

    }


    fun getInformationView(information: StayDetailk.DetailInformation): View {

        val viewDataBinding: DailyViewDetailDetailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_detail_information_data, this, false)

//        information.mContentList.forEach {
//            it.takeNotEmpty {
//                if (it.startsWith("**")) {
//
//                } else {
//
//                }
//            }
//        }

        return viewDataBinding.root
    }

    fun getContentBoldView(content: String): DailyTextView? {
        return content.letNotEmpty {
            DailyTextView(context).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f)
                setTextColor(context.resources.getColor(R.color.default_text_c323232))

                val spannableStringBuilder = SpannableStringBuilder()

                it.split("**").filter { !it.isTextEmpty() }.forEachIndexed { index, s ->
                    spannableStringBuilder.append(s)

                    if (index == 0) {
                        spannableStringBuilder.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).boldTypeface),
                                0, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
        }
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

    private fun getBreakfastView(): View {
        val viewDataBinding: DailyViewDetailBreakfastInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_breakfast_information_data, this, true)

        return viewDataBinding.root
    }

    private fun getWaitBookingInformationView(): View {
        val viewDataBinding = DataBindingUtil.inflate<LayoutStayDetailWaitforbookingDataBinding>(LayoutInflater.from(context), R.layout.layout_stay_detail_waitforbooking_data, this, false)

        viewDataBinding.textView.text = Html.fromHtml(context.getString(R.string.message_stay_waiting_reservation_guide))

        return viewDataBinding.root
    }
}
