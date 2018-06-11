package com.daily.dailyhotel.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.runTrue
import com.twoheart.dailyhotel.R

class DailyStayRoomBedDescriptionLayout : LinearLayout {
    constructor(context: Context?) : super(context) {
        initLayout(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initLayout(context)
    }

    private fun initLayout(context: Context?) {
        if (context == null) return

        fontTypeFace = FontManager.getInstance(context).mediumTypeface
        itemTextColor = context.resources.getColor(R.color.default_text_c929292)
        itemTopMargin = ScreenUtils.dpToPx(context, 2.0)

        val drawable = context.resources.getDrawable(R.drawable.shape_rect_stay_room_grid_description_background)
        val rect = Rect()
        drawable.getPadding(rect)

        horizontalPadding = rect.left + rect.right
    }

    private val paint = Paint()
    private var horizontalPadding = 0
    private val separator = ", "
    private val itemTextSize: Float = 11f
    private var itemTextColor: Int = 0
    private var itemTopMargin: Int = 0
    private lateinit var fontTypeFace: Typeface


    fun setData(list: MutableList<String>?) {
        removeAllViews()

        val listSize = list?.size ?: 0
        if (list == null || listSize == 0) {
            return
        }

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)

                val maxWidth = measuredWidth - horizontalPadding

                paint.textSize = ScreenUtils.dpToPx(context, itemTextSize.toDouble()).toFloat()
                paint.typeface = fontTypeFace

                var lineCount = 0
                var temp = ""

                list.forEachIndexed { _, string ->
                    val addString = if (temp.isTextEmpty()) string else separator + string

                    val textWidth = paint.measureText(temp + addString)
                    if (textWidth > maxWidth) {
                        (!temp.isTextEmpty()).runTrue {
                            val textView = getItemTextView(lineCount > 0)
                            lineCount++

                            textView.text = temp
                            temp = ""

                            addView(textView)
                        }

                        temp = addString.removePrefix(separator)
                    } else {
                        temp += addString
                    }
                }

                (!temp.isTextEmpty()).runTrue {
                    val textView = getItemTextView(lineCount > 0)
                    lineCount++

                    textView.text = temp
                    temp = ""

                    addView(textView)
                }

                return false
            }
        })
    }

    private fun getItemTextView(showTopMargin: Boolean): DailyTextView {
        return DailyTextView(context).apply {
            setBackgroundResource(R.drawable.shape_rect_stay_room_grid_description_background)

            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                if (showTopMargin) {
                    topMargin = itemTopMargin
                }

                setTextColor(itemTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, itemTextSize)
                gravity = Gravity.CENTER
                typeface = fontTypeFace
            }
        }
    }
}