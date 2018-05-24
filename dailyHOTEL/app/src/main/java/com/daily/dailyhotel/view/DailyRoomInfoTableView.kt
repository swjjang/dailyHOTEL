package com.daily.dailyhotel.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.DailyViewRoomTableInformationDataBinding

class DailyRoomInfoTableView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val SMALL_LEFT_VIEW_WIDTH = 95.0
        private const val SMALL_TABLE_ROW_LEFT_MARGIN = 8.0
        private const val SMALL_TABLE_ROW_SUB_LEFT_MARGIN = 2.0

        private const val LARGE_LEFT_VIEW_WIDTH = 145.0
        private const val LARGE_TABLE_ROW_LEFT_MARGIN = 12.0
        private const val LARGE_TABLE_ROW_SUB_LEFT_MARGIN = 1.0
    }

    private lateinit var dataBinding: DailyViewRoomTableInformationDataBinding
    private var itemCount: Int
    private val itemWidth: Int
    private val itemHeight: Int
    private val tableLineColorResId = R.color.default_line_cf0f0f0

    init {
        if (!this::dataBinding.isInitialized) {
            dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_room_table_information_data, this, true)
        }

        setBackgroundColor(context.resources.getColor(R.color.white))

        itemWidth = ScreenUtils.dpToPx(context, SMALL_LEFT_VIEW_WIDTH) // left padding 1dp 뺌
        itemHeight = ScreenUtils.dpToPx(context, 42.0) // 위아래 padding 1dp 뺌
        itemCount = 0

        dataBinding.tableLayout.setBackgroundColor(context.resources.getColor(tableLineColorResId))
    }

    fun setTitleText(stringResId: Int) {
        dataBinding.titleTextView.setText(stringResId)
    }

    fun setTitleText(text: String) {
        dataBinding.titleTextView.text = text
    }

    fun setTitleVisible(visible: Boolean) {
        dataBinding.titleTextLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setTitleTextSize(size: Float) {
        dataBinding.titleTextView.textSize = size
    }

    fun getItemCount(): Int {
        return itemCount
    }

    fun clearTableLayout() {
        if (dataBinding.tableLayout.childCount > 0) {
            dataBinding.tableLayout.removeAllViews()
        }

        itemCount = 0
    }

    fun addTableRow(title: String = "", description: String = "", subDescription: String = "", largeView: Boolean) {
        dataBinding.tableLayout.takeIf { itemCount > 0 }?.let {
            it.addView(getHorizontalLine())
        }

        val row: TableRow = TableRow(context).apply {
            val titleView = DailyTextView(context).apply {
                width = ScreenUtils.dpToPx(context, if (largeView) SMALL_LEFT_VIEW_WIDTH else LARGE_LEFT_VIEW_WIDTH)
                height = itemHeight
                setBackgroundColor(context.resources.getColor(R.color.information_background))
                setTextColor(context.resources.getColor(R.color.default_text_c929292))
                freezesText = true
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(ScreenUtils.dpToPx(context, if (largeView) SMALL_TABLE_ROW_LEFT_MARGIN else LARGE_TABLE_ROW_LEFT_MARGIN), 0, 0, 0)
                text = title
            }

            this.addView(titleView)
//            this.addView(getVerticalLine())

            val descriptionLayout = LinearLayout(context).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, itemHeight).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    orientation = LinearLayout.HORIZONTAL
                }

                val horizontalMargin = ScreenUtils.dpToPx(context, if (largeView) SMALL_TABLE_ROW_LEFT_MARGIN else LARGE_TABLE_ROW_LEFT_MARGIN)
                setPadding(horizontalMargin, 0, horizontalMargin, 0)
                setBackgroundColor(context.resources.getColor(R.color.white))
            }

            val descriptionView = DailyTextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }

                setTextColor(context.resources.getColor(R.color.default_text_c4d4d4d))
                freezesText = true
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                text = description
            }

            descriptionLayout.addView(descriptionView)

            val subDescriptionView = DailyTextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }

                setTextColor(context.resources.getColor(R.color.default_text_c929292))
                freezesText = true
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                text = subDescription
                setPadding(ScreenUtils.dpToPx(context, if (largeView) SMALL_TABLE_ROW_SUB_LEFT_MARGIN else LARGE_TABLE_ROW_SUB_LEFT_MARGIN), 0, 0, 0)
            }

            descriptionLayout.addView(subDescriptionView)

            this.addView(descriptionLayout)
        }

        itemCount++

        dataBinding.tableLayout.addView(row)
    }

//    private fun getVerticalLine(): View {
//        return View(context).apply {
//            setBackgroundColor(context.resources.getColor(tableLineColorResId))
//            layoutParams = ViewGroup.LayoutParams(ScreenUtils.dpToPx(context, 1.0), itemHeight)
//        }
//    }

    private fun getHorizontalLine(): View {
        return View(context).apply {
            setBackgroundColor(context.resources.getColor(tableLineColorResId))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(context, 1.0))
        }
    }

}