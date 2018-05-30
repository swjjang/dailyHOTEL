package com.daily.dailyhotel.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val paint = Paint()
    private val sampleText = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    private var horizontalPadding = 0
    private val separator = ", "
    private val endString = "개"

    fun setData(list: MutableList<String>?) {
        removeAllViews()

        val listSize = list?.size ?: 0

        if (list == null || listSize == 0) {
            return
        }

        val drawable: Drawable = context.resources.getDrawable(R.drawable.shape_rect_stay_room_grid_description_background)
        val rect = Rect()
        drawable.getPadding(rect)

        horizontalPadding = rect.left + rect.right

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)

                val maxWidth = measuredWidth - horizontalPadding

                paint.textSize = ScreenUtils.dpToPx(context, 11.0).toFloat()
                paint.typeface = FontManager.getInstance(context).mediumTypeface

                val maxLineLength = paint.breakText(sampleText, true, maxWidth.toFloat(), null)

                var lineCount = 0
                var temp = ""

                list.forEachIndexed { index, string ->
                    var addString = if (temp.isTextEmpty()) string else separator + string
                    if (index == listSize - 1) {
                        addString += endString
                    }

                    val sum = temp.length + addString.length
                    if (sum > maxLineLength) {
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
                if (showTopMargin) topMargin = ScreenUtils.dpToPx(context, 2.0)

                setTextColor(context.resources.getColor(R.color.default_text_c929292))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f)
                gravity = Gravity.CENTER
                typeface = FontManager.getInstance(context).mediumTypeface
            }
        }
    }
}