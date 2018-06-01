package com.daily.dailyhotel.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.support.v7.widget.GridLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.takeNotEmpty
import com.daily.dailyhotel.view.DailyRoomInfoGridView.ItemType.*
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewRoomGridInfoDataBinding

class DailyRoomInfoGridView : LinearLayout {

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    companion object {
        const val DEFAULT_SHOW_LINE_COUNT = 3

        private const val ANIMATION_DURATION = 200L

        private const val SMALL_TITLE_TEXT_SIZE = 16.0
        private const val SMALL_ICON_DRAW_PADDING = 6.0
        private const val SMALL_ITEM_TEXT_SIZE = 14.0
        private const val SMALL_ITEM_HEIGHT = 22.0
        private const val SMALL_LINE_MARGIN_TOP = 12.0

        private const val LARGE_TITLE_TEXT_SIZE = 18.0
        private const val LARGE_ICON_DRAW_PADDING = 10.0
        private const val LARGE_ITEM_TEXT_SIZE = 14.0
        private const val LARGE_ITEM_HEIGHT = 22.0
        private const val LARGE_LINE_MARGIN_TOP = 15.0
    }

    enum class ItemType {
        NONE, DOT, DOWN_CARET
    }

    private lateinit var viewDataBinding: DailyViewRoomGridInfoDataBinding
    var columnCount = 1
        set(value) {
            field = when {
                value < 1 -> 1
                else -> value
            }
            viewDataBinding.gridLayout.columnCount = field
            viewDataBinding.moreGridLayout.columnCount = field
        }

    init {
        if (!::viewDataBinding.isInitialized) {
            viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_room_grid_info_data, this, true)
        }

        orientation = LinearLayout.VERTICAL

        viewDataBinding.moreTextView.setOnClickListener({
            showMoreList()
        })
    }

//    fun setTitleText(textResId: Int) {
//        if (textResId == 0) {
//            return
//        }
//
//        viewDataBinding.titleTextView.setText(textResId)
//    }
//
//    fun setTitleText(text: String) {
//        viewDataBinding.titleTextView.text = text
//    }
//
//    fun setTitleVisible(visible: Boolean) {
//        viewDataBinding.titleTextLayout.visibility = if (visible) View.VISIBLE else View.GONE
//    }

    fun setData(title: String, type: ItemType, list: MutableList<String> = mutableListOf(), largeView: Boolean) {
        viewDataBinding.run {
            if (title.isTextEmpty()) {
                titleTextLayout.visibility = View.GONE
            } else {
                titleTextLayout.visibility = View.VISIBLE
                titleTextView.text = title
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (largeView) LARGE_TITLE_TEXT_SIZE.toFloat() else SMALL_TITLE_TEXT_SIZE.toFloat())
            }

            gridLayout.removeAllViews()
            moreGridLayout.removeAllViews()
            moreGridLayout.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT

            list.takeNotEmpty {
                gridLayout.visibility = View.VISIBLE
                moreGridLayout.visibility = View.INVISIBLE

                columnCount = gridLayout.columnCount
                val maxIndex = DEFAULT_SHOW_LINE_COUNT * columnCount
                val hasMore = list.size > maxIndex
                moreTextView.visibility = if (hasMore) View.VISIBLE else View.GONE

                it.forEachIndexed { index, text ->
                    val itemView: DailyTextView = getItemView(type, text, index >= columnCount, largeView)

                    if (index < maxIndex) {
                        gridLayout.addView(itemView)
                    } else {
                        moreGridLayout.addView(itemView)
                    }
                }

                val remainder = it.size % columnCount
                if (remainder != 0) {
                    for (index in 1..columnCount - remainder) {
                        val itemView = getItemView(NONE, "", index >= columnCount, largeView)

                        if (hasMore) {
                            moreGridLayout.addView(itemView)
                        } else {
                            gridLayout.addView(itemView)
                        }
                    }
                }
            }

            moreGridLayout.doOnPreDraw {
                if (largeView) {
                    LARGE_ITEM_HEIGHT + LARGE_LINE_MARGIN_TOP
                } else {
                    SMALL_ITEM_HEIGHT + SMALL_LINE_MARGIN_TOP
                }

                val rect = Rect()
                it.getLocalVisibleRect(rect)
                it.tag = it.height
                it.layoutParams.height = 0
                it.requestLayout()
            }

            moreGridLayout.requestLayout()
        }
    }

    private fun showMoreList() {
        val height = viewDataBinding.moreGridLayout.tag as Int
        if (height == 0 || viewDataBinding.moreGridLayout.visibility == View.VISIBLE) {
            return
        }

        val valueAnimator = ValueAnimator.ofInt(0, height).apply {
            addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
                if (valueAnimator == null) {
                    return@AnimatorUpdateListener
                }

                val value = valueAnimator.animatedValue as Int
                val layoutParams = viewDataBinding.moreGridLayout.layoutParams
                layoutParams.height = value
                viewDataBinding.moreGridLayout.requestLayout()
            })

            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    viewDataBinding.moreGridLayout.visibility = View.VISIBLE
                    viewDataBinding.moreTextView.visibility = View.GONE
                }

                override fun onAnimationEnd(animation: Animator) {
                    viewDataBinding.moreGridLayout.layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
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

    private fun getItemView(type: ItemType, text: String, showTopMargin: Boolean, largeView: Boolean): DailyTextView {
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
            if (iconResId != 0) {
                setDrawableCompatLeftAndRightFixedFirstLine(true)
                setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
                compoundDrawablePadding = ScreenUtils.dpToPx(context, if (largeView) LARGE_ICON_DRAW_PADDING else SMALL_ICON_DRAW_PADDING)
            }

            setTextColor(context.resources.getColor(textColorResId))
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (largeView) LARGE_ITEM_TEXT_SIZE.toFloat() else SMALL_ITEM_TEXT_SIZE.toFloat())

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f)

                if (showTopMargin) {
                    topMargin = ScreenUtils.dpToPx(context, if (largeView) LARGE_LINE_MARGIN_TOP else SMALL_LINE_MARGIN_TOP)
                }
            }

            this.layoutParams = params
        }
    }
}