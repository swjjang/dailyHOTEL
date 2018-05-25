package com.daily.dailyhotel.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.core.view.doOnPreDraw
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.StayDetailk
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.letNotEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailBreakfastInformationDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewDetailBreakfastInformationTableRowDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewDetailDetailInformationDataBinding
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

    fun setInformation(information: List<StayDetailk.DetailInformation.Item>?) {
        if (childCount > 0) {
            removeAllViews()
        }

        information.takeNotEmpty { it.forEach { addView(getInformationView(it)) } }
    }

    private fun getInformationView(information: StayDetailk.DetailInformation.Item): View {
        val viewDataBinding: DailyViewDetailDetailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_detail_information_data, this, false)

        viewDataBinding.titleTextView.text = information.title

        information.contentList.takeNotEmpty {
            it.forEachIndexed { index, content ->
                if (index < 3) {
                    viewDataBinding.informationLayout.addView(if (content.startsWith("**")) getContentBoldView(content) else getContentBulletView(content),
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                } else {
                    viewDataBinding.moreInformationLayout.addView(if (content.startsWith("**")) getContentBoldView(content) else getContentBulletView(content),
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
        }

        if (viewDataBinding.moreInformationLayout.childCount > 0) {
            viewDataBinding.moreTextView.visibility = View.VISIBLE
            viewDataBinding.moreTextView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    v?.setOnClickListener(null)

                    showMoreInformation(viewDataBinding)
                }
            })

            viewDataBinding.moreInformationLayout.visibility = View.INVISIBLE
            viewDataBinding.moreInformationLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            viewDataBinding.moreInformationLayout.doOnPreDraw {
                it.tag = it.height
                it.layoutParams.height = 0
                it.requestLayout()
            }
        } else {
            viewDataBinding.moreTextView.visibility = View.GONE
        }

        return viewDataBinding.root
    }

    private fun getContentBoldView(content: String): DailyTextView? {
        return content.letNotEmpty {
            DailyTextView(context).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
                setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
                setLineSpacing(1.0f, 1.0f)
                setPadding(0, ScreenUtils.dpToPx(context, 20.0), 0, 0)

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
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
            setLineSpacing(1.0f, 1.0f)
            compoundDrawablePadding = ScreenUtils.dpToPx(context, 10.0)
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_circle_b666666, 0, 0, 0)
            setPadding(0, ScreenUtils.dpToPx(context, 16.0), 0, 0)
        }
    }

    fun setBreakfastInformation(information: StayDetailk.BreakfastInformation?) {
        if (hasBreakfastInformation(information)) {
            val viewDataBinding: DailyViewDetailBreakfastInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_breakfast_information_data, this, true)

            information!!.items.takeNotEmpty {
                it.forEachIndexed { index, item ->
                    if (index > 0) {
                        viewDataBinding.breakfastTableLayout.addView(createLineView(), TableLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, 1.0))
                    }

                    createBreakfastView(viewDataBinding.breakfastTableLayout, item)
                }
            }

            information.descriptionList.takeNotEmpty {
                it.filter { !it.isTextEmpty() }.forEach {
                    viewDataBinding.informationLayout.addView(getContentBulletView(it))
                }
            }
        }
    }

    private fun hasBreakfastInformation(information: StayDetailk.BreakfastInformation?): Boolean {
        if (information?.items.isNotNullAndNotEmpty()) return true

        if (information?.descriptionList.isNotNullAndNotEmpty()) return true

        return false
    }

    private fun createBreakfastView(viewGroup: ViewGroup, item: StayDetailk.BreakfastInformation.Item) {
        val viewDataBinding: DailyViewDetailBreakfastInformationTableRowDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_breakfast_information_table_row_data, viewGroup, true)

        viewDataBinding.leftTextView.text = when {
            item.minAge < 0 && item.maxAge < 0 ->
                item.title

            item.minAge < 0 ->
                item.title + context.getString(R.string.label_stay_detail_under_age, item.maxAge)

            item.maxAge < 0 ->
                item.title + context.getString(R.string.label_stay_detail_over_age, item.minAge)

            else ->
                item.title + context.getString(R.string.label_stay_detail_range_age, item.minAge, item.maxAge)
        }

        viewDataBinding.rightTextView.text = if (item.amount == 0) context.getString(R.string.label_free) else DailyTextUtils.getPriceFormat(context, item.amount, false)
    }

    private fun createLineView(): View {
        return View(context).apply {
            setBackgroundColor(context.resources.getColor(R.color.default_line_cf0f0f0))
        }
    }

    private fun showMoreInformation(viewDataBinding: DailyViewDetailDetailInformationDataBinding) {
        val height = viewDataBinding.moreInformationLayout.tag as Int
        if (height == 0 || viewDataBinding.moreInformationLayout.visibility == View.VISIBLE) {
            return
        }

        ValueAnimator.ofInt(0, height).apply {
            addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
                valueAnimator?.let {
                    viewDataBinding.moreInformationLayout.apply {
                        layoutParams.height = valueAnimator.animatedValue as Int
                        requestLayout()
                    }
                }
            })

            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    viewDataBinding.moreInformationLayout.visibility = View.VISIBLE
                    viewDataBinding.moreTextView.visibility = View.GONE
                }

                override fun onAnimationEnd(animation: Animator) {
                    removeAllUpdateListeners()
                    removeAllListeners()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }.start()
    }
}
