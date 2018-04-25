package com.daily.dailyhotel.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.widget.GridLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.daily.base.util.ExLog
import com.daily.dailyhotel.view.DailyRoomGridInfoView.ItemType.*
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewRoomGridInfoDataBinding
import com.twoheart.dailyhotel.databinding.DailyViewRoomGridItemDataBinding

class DailyRoomGridInfoView : LinearLayout {

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
        setBackgroundColor(Color.RED)

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

    fun setColumnCount(count: Int) {
        var newCount = 1

        if (count > 0) {
            newCount = count
        }

        viewDataBinding.gridLayout.columnCount = newCount
        viewDataBinding.moreGridLayout.columnCount = newCount
    }

    fun setData(type: ItemType, list: MutableList<String> = mutableListOf()) {
        if (list.isEmpty()) {
            viewDataBinding.gridLayout.visibility = View.GONE
            viewDataBinding.moreGridLayout.visibility = View.GONE
            viewDataBinding.moreTextView.visibility = View.GONE
            return
        }

        viewDataBinding.moreGridLayout.removeAllViews()

        viewDataBinding.gridLayout.visibility = View.VISIBLE
        viewDataBinding.moreGridLayout.visibility = View.VISIBLE
        viewDataBinding.moreTextView.visibility = View.VISIBLE

        val columnCount = viewDataBinding.gridLayout.columnCount
        val maxIndex = DEFAULT_SHOW_LINE_COUNT * columnCount
        val hasMore = list.size > maxIndex

        list.forEachIndexed { index, text ->
            if (index < maxIndex) {
                viewDataBinding.gridLayout.addView(getItemView(type, text))
            } else {
                viewDataBinding.moreGridLayout.addView(getItemView(type, text))
            }
        }

        val remainder = list.size % columnCount
        if (remainder != 0) {
            for (index in 1..columnCount - remainder) {
                if (hasMore) {
                    viewDataBinding.moreGridLayout.addView(getItemView(NONE, ""))
                } else {
                    viewDataBinding.gridLayout.addView(getItemView(NONE, ""))
                }
            }
        }

        viewDataBinding.moreGridLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                try {
                    viewDataBinding.moreGridLayout.viewTreeObserver.removeOnPreDrawListener(this)
                    viewDataBinding.moreGridLayout.tag = viewDataBinding.moreGridLayout.height

                    val params = viewDataBinding.moreGridLayout.layoutParams as LinearLayout.LayoutParams
                    if (params != null) {
                        params.height = 0
                        viewDataBinding.moreGridLayout.layoutParams = params
                    }
                } catch (e: Exception) {
                    ExLog.e(e.toString())
                }

                return false
            }
        })
    }

    private fun isShowMoreList(): Boolean {
        return viewDataBinding.moreGridLayout.height > 0
    }

    fun showMoreList() {

        val height = viewDataBinding.moreGridLayout.tag as Int
        if (height == 0) {
            return
        }

        if (isShowMoreList()) {
            return
        }

        val valueAnimator = ValueAnimator.ofInt(0, height)
        valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            if (valueAnimator == null) {
                return@AnimatorUpdateListener
            }

            val value = valueAnimator.animatedValue as Int
            val layoutParams = viewDataBinding.moreGridLayout.layoutParams
            layoutParams.height = value
            viewDataBinding.moreGridLayout.requestLayout()
        })

        valueAnimator.duration = 200
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                viewDataBinding.moreTextView.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animator) {
                valueAnimator.removeAllUpdateListeners()
                valueAnimator.removeAllListeners()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })

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

        val dataBinding: DailyViewRoomGridItemDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)
                , R.layout.daily_view_room_grid_item_data, null, false)

        if (iconResId == 0) {
            dataBinding.iconView.visibility = View.GONE
        } else {
            dataBinding.iconView.visibility = View.VISIBLE
            dataBinding.iconView.setImageResource(iconResId)
        }

        dataBinding.textView.setTextColor(context.resources.getColor(textColorResId))
        dataBinding.textView.text = text

        return dataBinding.root.apply {
            val params = GridLayout.LayoutParams().apply {
//                width = GridLayout.LayoutParams.WRAP_CONTENT
//                height = GridLayout.LayoutParams.WRAP_CONTENT

                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f)
            }

            this.layoutParams = params
        }
    }
}