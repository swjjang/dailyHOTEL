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
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailRefundInformationDataBinding
import com.twoheart.dailyhotel.databinding.LayoutStayDetailWaitforbookingDataBinding
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan

class DailyDetailCheckInformationView : LinearLayout {

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

    fun setInformation(information: StayDetail.CheckInformation) {
        removeAllViews()

        if (information.contentList.isNotNullAndNotEmpty()) {
            createInformationView(information, information.waitingForBooking)

            if (information.waitingForBooking) {
                createWaitForBookingInformationView(true)
            }
        } else {
            if (information.waitingForBooking) {
                createWaitForBookingInformationView(false)
            }
        }
    }

    private fun createInformationView(information: StayDetail.CheckInformation, hasWaitingForBooking: Boolean) {
        DataBindingUtil.inflate<DailyViewDetailRefundInformationDataBinding>(LayoutInflater.from(context), R.layout.daily_view_detail_refund_information_data, this, true).apply {
            titleTextView.text = information.title

            information.contentList.takeNotEmpty {
                it.forEach {
                    informationLayout.addView(if (it.startsWith("**")) getContentBoldView(it) else getContentBulletView(it))
                }
            }

            informationLayout.setPadding(0, 0, 0, if (hasWaitingForBooking) 0 else ScreenUtils.dpToPx(context, 30.0))
        }
    }

    private fun getContentBoldView(content: String): DailyTextView {
        return DailyTextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
            setLineSpacing(1.0f, 1.0f)
            setPadding(0, ScreenUtils.dpToPx(context, 20.0), 0, 0)

            val spannableStringBuilder = SpannableStringBuilder()

            content.split("**").filter { !it.isTextEmpty() }.forEachIndexed { index, s ->
                spannableStringBuilder.append(s)

                if (index == 0) {
                    spannableStringBuilder.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).mediumTypeface),
                            0, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            text = spannableStringBuilder
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
            setPadding(0, ScreenUtils.dpToPx(context, 16.0), 0, 0)
        }
    }

    private fun createWaitForBookingInformationView(hasCheckInformation: Boolean) {
        DataBindingUtil.inflate<LayoutStayDetailWaitforbookingDataBinding>(LayoutInflater.from(context), R.layout.layout_stay_detail_waitforbooking_data, this, true).apply {
            topLineView.visibility = if (hasCheckInformation) View.VISIBLE else View.GONE
            contentTextView.text = Html.fromHtml(context.getString(R.string.message_stay_waiting_reservation_guide))
        }
    }
}
