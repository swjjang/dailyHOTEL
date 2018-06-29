package com.daily.dailyhotel.screen.home.stay.inbound.detail.room

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.daily.base.util.ScreenUtils
import com.daily.dailyhotel.entity.Room
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.LayoutStayRoomDetailDataBinding

class StayRoomItemView : RelativeLayout {
    companion object {
        const val MIN_SCALE_VALUE = 0.865f
        const val MAX_SCALE_VALUE = 1.0f
        const val RETURN_SCALE_GAP = 0.3f
        const val ANIMATION_DURATION = 200
    }

    private lateinit var viewDataBinding: LayoutStayRoomDetailDataBinding
    private var backgroundPaddingTop: Int = 0
    private var backgroundPaddingLeft: Int = 0
    private var backgroundPaddingRight: Int = 0
    private var minWidth: Int = ViewGroup.LayoutParams.MATCH_PARENT
    private var minScale = MIN_SCALE_VALUE
    private var downReturnScale = MIN_SCALE_VALUE
    private var upReturnScale = MAX_SCALE_VALUE
    private val room: Room = Room()

    constructor(context: Context?) : super(context) {
        initLayout(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initLayout(context, attrs)
    }

    private fun initLayout(context: Context?, attrs: AttributeSet?) {
        if (context == null) {
            return
        }

        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_stay_room_detail_data, this, true)

        attrs?.run {
            val typedArray: TypedArray = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.background))

            typedArray.getDrawable(0)?.run {
                val rect = Rect()
                getPadding(rect)

                backgroundPaddingLeft = rect.left
                backgroundPaddingRight = rect.right
                backgroundPaddingTop = rect.top
            }
        }

        minWidth = (ScreenUtils.getScreenWidth(context) * StayRoomAdapter.MENU_WIDTH_RATIO - backgroundPaddingLeft - backgroundPaddingRight).toInt()
        var minScale = getMinScale()
        val scaleGap = (MAX_SCALE_VALUE - minScale) * RETURN_SCALE_GAP
        downReturnScale = minScale + scaleGap
        upReturnScale = MAX_SCALE_VALUE - scaleGap
    }

    fun setScale(scale: Float) {
        val minScale = getMinScale()

        val toScale = when {
            scale < minScale -> {
                minScale
            }

            scale > MAX_SCALE_VALUE -> {
                MAX_SCALE_VALUE
            }

            else -> {
                scale
            }
        }

        val width = ScreenUtils.getScreenWidth(context) * toScale
        layoutParams.width = width.toInt()
    }

    fun getMinWidth(): Int {
        var screenWidth = ScreenUtils.getScreenWidth(context)

        return if (minWidth <= 0) {
            (screenWidth * StayRoomAdapter.MENU_WIDTH_RATIO - backgroundPaddingLeft - backgroundPaddingRight).toInt()
        } else {
            minWidth
        }
    }

    fun getMinScale(): Float {
        return getMinWidth().toFloat() / ScreenUtils.getScreenWidth(context)
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)

        background?.run {
            val rect = Rect()
            getPadding(rect)

            backgroundPaddingLeft = rect.left
            backgroundPaddingRight = rect.right
            backgroundPaddingTop = rect.top
        }
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        super.setBackgroundDrawable(background)

        background?.run {
            val rect = Rect()
            getPadding(rect)

            backgroundPaddingLeft = rect.left
            backgroundPaddingRight = rect.right
            backgroundPaddingTop = rect.top
        }
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)

        background?.run {
            val rect = Rect()
            getPadding(rect)

            backgroundPaddingLeft = rect.left
            backgroundPaddingRight = rect.right
            backgroundPaddingTop = rect.top
        }
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)

        backgroundPaddingLeft = 0
        backgroundPaddingRight = 0
        backgroundPaddingTop = 0
    }
}