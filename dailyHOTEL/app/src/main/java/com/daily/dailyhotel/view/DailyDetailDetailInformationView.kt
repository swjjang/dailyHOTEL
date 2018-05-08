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
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetailk
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBreakfastInformationDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewDetailBreakfastInformationTableRowDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewDetailDetailInformationDataBinding
import com.twoheart.dailyhotel.databinding.LayoutStayDetailWaitforbookingDataBinding
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan
import java.util.*

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

    fun setInformation(information: List<StayDetailk.DetailInformation.Item>?) {
        information.takeNotEmpty {
            it.forEach {
                addView(getInformationView(it))
            }
        }
    }

    private fun getInformationView(information: StayDetailk.DetailInformation.Item): View {
        val viewDataBinding: DailyViewDetailDetailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_detail_information_data, this, false)

        viewDataBinding.titleTextView.text = information.title

        information.contentList.takeNotEmpty {
            it.forEach {
                viewDataBinding.informationLayout.addView(if (it.startsWith("**")) getContentBoldView(it) else getContentBulletView(it))
            }
        }

        return viewDataBinding.root
    }

    private fun getContentBoldView(content: String): DailyTextView? {
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

    fun setBreakfastInformation(information: StayDetailk.BreakfastInformation?) {
        information?.let {
            val viewDataBinding: DailyViewDetailBreakfastInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_breakfast_information_data, this, true)

            it.items.takeNotEmpty {
                it.forEachIndexed { index, item ->
                    if (index > 0) {
                        viewDataBinding.breakfastTableLayout.addView(getLineView(), TableLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, 1.0))
                    }

                    getBreakfastItemView(viewDataBinding.breakfastTableLayout, item)
                }

            }

            it.description.takeNotEmpty {
                it.filter { !it.isTextEmpty() }.forEach {
                    viewDataBinding.informationLayout.addView(getContentBulletView(it))
                }
            }
        }
    }

    private fun getBreakfastItemView(viewGroup: ViewGroup, item: StayDetailk.BreakfastInformation.Item) {
        val viewDataBinding: DailyViewDetailBreakfastInformationTableRowDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_breakfast_information_table_row_data, viewGroup, true)

        var leftText: String? = null

        when {
            item.minAge < 0 && item.maxAge < 0 -> {
                leftText = "(전연령)"
            }

            item.maxAge < 0 -> {
                leftText = item.title + String.format(Locale.KOREA, "(%d세 이상)", item.minAge)
            }

            else -> {
                leftText = item.title + String.format(Locale.KOREA, "(%d ~ %d세 이하)", item.minAge, item.maxAge)
            }
        }

        viewDataBinding.leftTextView.text = leftText
        viewDataBinding.rightTextView.text = DailyTextUtils.getPriceFormat(context, item.amount, false)
    }

    private fun getLineView(): View {
        return View(context).apply {
            setBackgroundColor(context.resources.getColor(R.color.default_text_c323232))
        }
    }
}
