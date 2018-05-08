package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewDetailDailyCommentDataBinding

class DailyDetailDailyCommentView : ConstraintLayout {
    private lateinit var viewDataBinding: DailyViewDetailDailyCommentDataBinding

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
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_daily_comment_data, this, true)

    }

    fun setComments(comments: List<String>?) {
        viewDataBinding.dailyCommentsLayout.removeAllViews()

        comments.takeNotEmpty {
            it.forEach {
                viewDataBinding.dailyCommentsLayout.addView(getCommentView(it), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun getCommentView(comment: String): View {
        return DailyTextView(context).apply {
            text = comment
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f)
            setTextColor(context.resources.getColor(R.color.default_text_c323232))
            setDrawableCompatLeftAndRightFixedFirstLine(true)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_xs, 0, 0, 0)
        }
    }
}
