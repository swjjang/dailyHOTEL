package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailTrueReviewExDataBinding
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

    fun setPreviewTrueReview(review: String?, ratingValue: String?, reviewer: String?) {
        viewDataBinding.previewTrueReviewTextView.text = review
        viewDataBinding.ratingValueTextView.text = ratingValue
        viewDataBinding.reviewerTextView.text = reviewer
    }

    fun setShowTrueReviewButtonVisible(visible: Boolean) {
        viewDataBinding.showTrueReviewTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setShowTrueReviewButtonText(reviewCount: Int) {
        viewDataBinding.showTrueReviewTextView.text = context.getString(R.string.label_detail_view_show_true_review, reviewCount)
    }
}
