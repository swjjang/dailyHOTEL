package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailTrueReviewExDataBinding
import com.twoheart.dailyhotel.util.DailyCalendar
import java.text.DecimalFormat

class DailyDetailTrueReviewExView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailTrueReviewExDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_true_review_ex_data, this, true)
    }

    fun setSatisfactionVisible(visible: Boolean) {
        viewDataBinding.satisfactionGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setSatisfaction(satisfaction: Int, ratingCount: Int) {
        viewDataBinding.satisfactionTextView.text = if (satisfaction > 0) context.getString(R.string.label_stay_detail_satisfaction, satisfaction) else null
        viewDataBinding.ratingCountTextView.text = if (ratingCount > 0) context.getString(R.string.label_stay_detail_rating, DecimalFormat("###,##0").format(ratingCount)) else null
    }

    fun setPreviewTrueReviewVisible(visible: Boolean) {
        viewDataBinding.previewTrueReviewGroup.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setPreviewTrueReview(review: String?, ratingValue: String?, reviewer: String?, createdAt: String?) {
        viewDataBinding.previewTrueReviewTextView.text = review
        viewDataBinding.ratingValueTextView.text = ratingValue

        var name = reviewer

        if (DailyTextUtils.isTextEmpty(name)) {
            name = context.getString(R.string.label_customer)
        }

        try {
            if (DailyTextUtils.isTextEmpty(createdAt)) {
                viewDataBinding.reviewerTextView.text = name
            } else {
                val separator = "ㅣ"
                val reviewerLength = name!!.length
                val spannableStringBuilder = SpannableStringBuilder(name + separator + DailyCalendar.convertDateFormatString(createdAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"))
                spannableStringBuilder.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.default_line_ce7e7e7)), //
                        reviewerLength, reviewerLength + separator.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                viewDataBinding.reviewerTextView.text = spannableStringBuilder
            }
        } catch (e: Exception) {
            ExLog.d(e.toString())
        }
    }

    fun setShowTrueReviewButtonVisible(visible: Boolean) {
        viewDataBinding.showTrueReviewTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setShowTrueReviewButtonText(reviewCount: Int) {
        viewDataBinding.showTrueReviewTextView.text = context.getString(R.string.label_detail_view_show_true_review, reviewCount)
    }

    fun setTrueReviewClickListener(listener: View.OnClickListener) {
        viewDataBinding.showTrueReviewTextView.setOnClickListener(listener)
    }
}
