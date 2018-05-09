package com.daily.dailyhotel.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.GridLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.daily.base.util.ExLog
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.view.DailyRoomInfoGridView.ItemType.*
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewRoomGridInfoDataBinding

class DailyRoomInfoGridView : LinearLayout {

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    companion object {
        const val DEFAULT_SHOW_LINE_COUNT = 3
    }

    enum class ItemType {
        NONE, DOT, DOWN_CARET
    }

    private lateinit var viewDataBinding: DailyViewRoomGridInfoDataBinding

    init {
        if (!::viewDataBinding.isInitialized) {
            viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_room_grid_info_data, this, true)
        }

        orientation = LinearLayout.VERTICAL

        viewDataBinding.moreTextView.setOnClickListener({
            showMoreList()
        })
    }

    fun setTitleText(textResId: Int) {
        if (textResId == 0) {
            return
        }

        viewDataBinding.titleTextView.setText(textResId)
    }

    fun setTitleText(text: String) {
        viewDataBinding.titleTextView.text = text
    }

    fun setTitleVisible(visible: Boolean) {
        viewDataBinding.titleTextView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setColumnCount(count: Int) {
        var newCount = 1

        if (count > 0) {
            newCount = count
        }

        viewDataBinding.gridLayout.columnCount = newCount
        viewDataBinding.moreGridLayout.columnCount = newCount
    }

    fun setData(type: ItemType, list: MutableList<String> = mutableListOf()) {
        viewDataBinding.run {
            if (list.isEmpty()) {
                gridLayout.visibility = View.GONE
                moreGridLayout.visibility = View.GONE
                moreTextView.visibility = View.GONE
                return
            }

            moreGridLayout.removeAllViews()

            gridLayout.visibility = View.VISIBLE
            moreGridLayout.visibility = View.VISIBLE
            moreTextView.visibility = View.VISIBLE

            val columnCount = gridLayout.columnCount
            val maxIndex = DEFAULT_SHOW_LINE_COUNT * columnCount
            val hasMore = list.size > maxIndex

            list.forEachIndexed { index, text ->
                if (index < maxIndex) {
                    gridLayout.addView(getItemView(type, text))
                } else {
                    moreGridLayout.addView(getItemView(type, text))
                }
            }

            val remainder = list.size % columnCount
            if (remainder != 0) {
                for (index in 1..columnCount - remainder) {
                    if (hasMore) {
                        moreGridLayout.addView(getItemView(NONE, ""))
                    } else {
                        gridLayout.addView(getItemView(NONE, ""))
                    }
                }
            }

            moreGridLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    try {
                        moreGridLayout.viewTreeObserver.removeOnPreDrawListener(this)
                        moreGridLayout.tag = moreGridLayout.height

                        (moreGridLayout.layoutParams as? LinearLayout.LayoutParams)?.let {
                            it.height = 0
                            moreGridLayout.layoutParams = it
                        }
                    } catch (e: Exception) {
                        ExLog.e(e.toString())
                    }

                    return false
                }
            })
        }
    }

    private fun isShowMoreList(): Boolean {
        return viewDataBinding.moreGridLayout.height > 0
    }

    private fun showMoreList() {

        val height = viewDataBinding.moreGridLayout.tag as Int
        if (height == 0) {
            return
        }

        if (isShowMoreList()) {
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

            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
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
        }

        valueAnimator.start()
    }

    private fun getItemView(type: ItemType, text: String): View {
        val iconResId: Int
        val textColorResId: Int

        when (type) {
            DOT -> {
                iconResId = R.drawable.info_ic_text_dot_black
                textColorResId = R.color.default_text_c323232
            }

            DOWN_CARET -> {
                iconResId = R.drawable.info_ic_text_dot_grey
                textColorResId = R.color.default_text_c929292
            }

            else -> {
                iconResId = 0
                textColorResId = R.color.default_text_c2284dc
            }
        }

        return DailyTextView(context).apply {
            if (iconResId != 0) {
                setDrawableCompatLeftAndRightFixedFirstLine(true)
                setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
            }

            setTextColor(context.resources.getColor(textColorResId))
            this.text = text

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f)
            }

            this.layoutParams = params
        }
    }
}