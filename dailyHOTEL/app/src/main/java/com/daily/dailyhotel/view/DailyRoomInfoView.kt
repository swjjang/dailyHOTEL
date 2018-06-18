package com.daily.dailyhotel.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.support.v7.widget.GridLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.util.takeNotEmpty
import com.daily.dailyhotel.view.DailyRoomInfoView.ItemType.*
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewRoomInfoDataBinding
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan

class DailyRoomInfoView : LinearLayout {

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    companion object {
        const val DEFAULT_SHOW_LINE_COUNT = 3

        private const val ANIMATION_DURATION = 200L

        private const val SMALL_TITLE_TEXT_SIZE = 16.0
        private const val SMALL_ICON_DRAW_PADDING = 6.0
        private const val SMALL_LINE_MARGIN_TOP = 12.0
        private const val SMALL_MORE_TEXT_MARGIN_TOP = 12.0

        private const val LARGE_TITLE_TEXT_SIZE = 18.0
        private const val LARGE_ICON_DRAW_PADDING = 10.0
        private const val LARGE_LINE_MARGIN_TOP = 17.0
        private const val LARGE_MORE_TEXT_MARGIN_TOP = 14.0

        private const val NORMAL_ITEM_TEXT_SIZE = 14f
        private const val BOLD_ITEM_TEXT_SIZE = 16f
        private const val BOLD_FIRST_LINE_MARGIN_TOP = 4.0
        private const val BOLD_LINE_MARGIN_TOP = 20.0
    }

    enum class ItemType {
        NONE, DOT, DOWN_CARET
    }

    private lateinit var viewDataBinding: DailyViewRoomInfoDataBinding
    var columnCount = 1
        set(value) {
            field = when {
                value < 1 -> 1
                else -> value
            }
        }

    init {
        if (!::viewDataBinding.isInitialized) {
            viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_room_info_data, this, true)
        }

        orientation = LinearLayout.VERTICAL

        viewDataBinding.moreTextView.setOnClickListener({
            showMoreList()
        })
    }

    fun setData(title: String, type: ItemType, list: MutableList<String> = mutableListOf(), largeView: Boolean, showAll: Boolean = false) {
        viewDataBinding.run {
            if (title.isTextEmpty()) {
                titleTextLayout.visibility = View.GONE
            } else {
                titleTextLayout.visibility = View.VISIBLE
                titleTextView.text = title
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (largeView) LARGE_TITLE_TEXT_SIZE.toFloat() else SMALL_TITLE_TEXT_SIZE.toFloat())
            }

            (moreTextView.layoutParams as LinearLayout.LayoutParams).run {
                topMargin = ScreenUtils.dpToPx(context, if (largeView) LARGE_MORE_TEXT_MARGIN_TOP else SMALL_MORE_TEXT_MARGIN_TOP)
            }

            itemLayout.removeAllViews()
            moreItemLayout.removeAllViews()
            moreItemLayout.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT

            list.takeNotEmpty {
                itemLayout.visibility = View.VISIBLE
                moreItemLayout.visibility = View.INVISIBLE

                val maxIndex = DEFAULT_SHOW_LINE_COUNT * columnCount
                val hasMore = list.size > maxIndex
                moreTextView.visibility = if (hasMore && !showAll) View.VISIBLE else View.GONE

                var subLayout: LinearLayout = getSubLayout(largeView, false, false)
                it.forEachIndexed { index, text ->
                    val needSubLayout = (index % columnCount) == 0
                    val showTopMargin = index >= columnCount
                    val isBold = columnCount == 1 && text.startsWith("**")

                    needSubLayout.runTrue {
                        subLayout = getSubLayout(largeView, showTopMargin, isBold)
                        subLayout.removeAllViews()

                        if (showAll || index < maxIndex) {
                            itemLayout.addView(subLayout)
                        } else {
                            moreItemLayout.addView(subLayout)
                        }
                    }

                    val itemView: DailyTextView = getItemView(type, text, largeView, isBold)
                    subLayout.addView(itemView)
                }

                val remainder = it.size % columnCount
                if (remainder != 0) {
                    for (index in 1..columnCount - remainder) {
                        val itemView = getItemView(NONE, "", largeView, false)
                        subLayout.addView(itemView)
                    }
                }
            }

            moreItemLayout.doOnPreDraw {
                val rect = Rect()
                it.getLocalVisibleRect(rect)
                it.tag = it.height
                it.layoutParams.height = 0
                it.requestLayout()
            }

            moreItemLayout.requestLayout()
        }
    }

    private fun showMoreList() {
        val height = viewDataBinding.moreItemLayout.tag as Int
        if (height == 0 || viewDataBinding.moreItemLayout.visibility == View.VISIBLE) {
            return
        }

        val valueAnimator = ValueAnimator.ofInt(0, height).apply {
            addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
                if (valueAnimator == null) {
                    return@AnimatorUpdateListener
                }

                val value = valueAnimator.animatedValue as Int
                val layoutParams = viewDataBinding.moreItemLayout.layoutParams
                layoutParams.height = value
                viewDataBinding.moreItemLayout.requestLayout()
            })

            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    viewDataBinding.moreItemLayout.visibility = View.VISIBLE
                    viewDataBinding.moreTextView.visibility = View.GONE
                }

                override fun onAnimationEnd(animation: Animator) {
                    viewDataBinding.moreItemLayout.layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
                    removeAllUpdateListeners()
                    removeAllListeners()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }

        valueAnimator.start()
    }

    private fun getSubLayout(largeView: Boolean, showTopMargin: Boolean, isBold: Boolean): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                width = LinearLayout.LayoutParams.MATCH_PARENT
                height = LinearLayout.LayoutParams.WRAP_CONTENT

                topMargin = when {
                    isBold && showTopMargin -> {
                        ScreenUtils.dpToPx(context, BOLD_LINE_MARGIN_TOP)
                    }

                    isBold -> {
                        ScreenUtils.dpToPx(context, BOLD_FIRST_LINE_MARGIN_TOP)
                    }

                    showTopMargin -> {
                        ScreenUtils.dpToPx(context, if (largeView) LARGE_LINE_MARGIN_TOP else SMALL_LINE_MARGIN_TOP)
                    }

                    else -> {
                        0
                    }
                }
            }
        }
    }

    private fun getItemView(type: ItemType, itemText: String, largeView: Boolean, isBold: Boolean): DailyTextView {
        val iconResId: Int
        val textColorResId: Int

        when (type) {
            DOT -> {
                iconResId = R.drawable.shape_circle_b666666
                textColorResId = R.color.default_text_c4d4d4d
            }

            DOWN_CARET -> {
                iconResId = R.drawable.vector_ic_check_xs
                textColorResId = R.color.default_text_ccf9e5e
            }

            else -> {
                iconResId = 0
                textColorResId = R.color.default_text_c929292
            }
        }

        return DailyTextView(context).apply {
            setTextColor(context.resources.getColor(textColorResId))

            when (isBold) {
                true -> {
                    val spannableStringBuilder = SpannableStringBuilder()

                    itemText.split("**").filter { !it.isTextEmpty() }.forEachIndexed { index, s ->
                        spannableStringBuilder.append(s)

                        if (index == 0) {
                            spannableStringBuilder.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).mediumTypeface),
                                    0, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }

                    text = spannableStringBuilder
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, BOLD_ITEM_TEXT_SIZE)
                }

                false -> {
                    if (iconResId != 0) {
                        setDrawableCompatLeftAndRightFixedFirstLine(true)
                        setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
                        compoundDrawablePadding = ScreenUtils.dpToPx(context, if (largeView) LARGE_ICON_DRAW_PADDING else SMALL_ICON_DRAW_PADDING)
                    }

                    this.text = itemText
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, NORMAL_ITEM_TEXT_SIZE)
                }
            }

            this.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
        }
    }
}